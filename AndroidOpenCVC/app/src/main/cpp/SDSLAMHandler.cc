#include "SDSLAMHandler.h"
#include <jni.h>
#include <sstream>
#include <opencv2/imgproc.hpp>

#define APPNAME "AR_SD_SLAM"

class System;

using namespace Eigen;


extern "C"{
    jlong JNICALL Java_aopencvc_utils_SLAMHandler_CreateSLAM(JNIEnv *env, jobject instance) {
        jlong result = 0;

        try {
            //cv::Ptr<SLAM::SystemHandler> slam = cv::makePtr<SLAM::SystemHandler>(cv::makePtr<SLAM::SystemHandler>());
            result = (jlong) new SLAM::SDSLAMHandler();
        } catch (...) {
            jclass je = env->FindClass("java/lang/Exception");
            env->ThrowNew(je, "Unknown exception in JNI code of CreateSLAM()");
            return 0;
        }
        return result;
    }
}
extern "C"{
    jstring JNICALL Java_aopencvc_utils_SLAMHandler_TrackFrame(JNIEnv *env, jobject instance,
                                                               jlong slam, jint param, jlong img,
                                                                jlong cameraRotation, jlong planeEq,
                                                               jlong cameraPose) {
        std::stringstream ss;
        ss << "Hello from C++: ";

        try {
            cv::Mat * cimg = (cv::Mat*) img;
            cv::Mat * cRot = (cv::Mat*) cameraRotation;
            cv::Mat * plane = (cv::Mat*) planeEq;
            cv::Mat * pose = (cv::Mat*) cameraPose;
            ss << ((SLAM::SDSLAMHandler*)slam)->TrackMonocular(*cimg, *cRot, *plane, *pose);
        } catch (...) {
            jclass je = env->FindClass("java/lang/Exception");
            env->ThrowNew(je, "Unknown exception in JNI code of TrackFrame()");
            return 0;
        }

        return env->NewStringUTF(ss.str().c_str());
    }
}


using std::vector;

namespace SLAM {

    SDSLAMHandler::SDSLAMHandler() {
        counter_ = 0;

        // Set camera config
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();
        config.SetCameraIntrinsics(640, 360, 673.861075, 677.584410,
                                   384.323789, 227.457859);
        config.SetCameraDistortion(-0.350240, 1.384144,
                                   -0.008788, -0.022544, -2.385009);
        config.SetUsePattern(true);
        selectKP = true;
        nKeyFrames = 0;

        slam = new SD_SLAM::System(SD_SLAM::System::MONOCULAR);
    }

    int SDSLAMHandler::TrackMonocular(cv::Mat &img, cv::Mat &rotation,
                                      cv::Mat &plane, cv::Mat &pose) {
        // Convert to gray to track
        cv::Mat gray;
        cv::cvtColor(img, gray, cv::COLOR_RGB2GRAY);
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();


        Eigen::Matrix4d ePose = slam->TrackMonocular(gray);

        // Draw in image
        DrawFrame(slam, img, pose, plane);

        counter_++;
        return img.cols*1000 + img.rows*1000000 + counter_;
        //return (int) pose(0,3);
    }


    SD_SLAM::MapPoint * SDSLAMHandler::getMapPoints(){
        return CurrentMP;
    }

    SD_SLAM::Tracking::eTrackingState SDSLAMHandler::getTrackingStatus(){
        return trackerStatus;
    }

    void SDSLAMHandler::DrawFrame(SD_SLAM::System * slam, cv::Mat &img, cv::Mat &pose,
                                  cv::Mat &plane) {
        SD_SLAM::Tracking * tracker = slam->GetTracker();
        trackerStatus = tracker->GetLastState();
        // Not initialized
        if (trackerStatus == SD_SLAM::Tracking::NOT_INITIALIZED) {
            std::vector<int> vMatches = tracker->GetInitialMatches();
            std::vector<cv::KeyPoint> vIniKeys = tracker->GetInitialFrame().mvKeys;
            std::vector<cv::KeyPoint> vCurrentKeys = tracker->GetCurrentFrame().mvKeys;

            circle(img, cv::Point(50,50),5, cv::Scalar(0,255,0),CV_FILLED, 8,0);
        }

        // Status OK
        if (trackerStatus == SD_SLAM::Tracking::OK) {
            SD_SLAM::Frame &currentFrame = tracker->GetCurrentFrame();
            std::vector<cv::KeyPoint> vCurrentKeys = currentFrame.mvKeys;
            //std::vector<SD_SLAM::MapPoint *> vCurrentMP = currentFrame.mvpMapPoints;
            nKeyFrames = slam->GetMap()->GetAllKeyFrames().size();
            Eigen::Matrix4d mTcw = currentFrame.GetPose();
            Eigen::Matrix3d mRcw = mTcw.block<3, 3>(0, 0);
            Eigen::Vector3d mtcw = mTcw.block<3, 1>(0, 3);

            Eigen::Matrix3d rotMat;
            rotMat.setZero();
            rotMat(0,0) = 1;
            rotMat(1,1) = -1;
            rotMat(2,2) = -1;
            Eigen::Matrix3d mRcwRotated = rotMat * mRcw;
            Eigen::Vector3d mtcwRotated = rotMat * mtcw;

            cv::Mat result = *new cv::Mat(1, 4, CV_64F);


            Eigen::Matrix4d newPose;
            newPose.setZero();
            newPose.block<3, 3>(0, 0) = mRcwRotated;
            newPose.block<3, 1>(0, 3) = mtcwRotated;
            newPose.block<1,4>(3,0) = mTcw.block<1,4>(3,0);
            for (int i = 0;i<pose.rows * pose.cols;i++){
                pose.at<double>(i%4,i/4) = newPose(i);
            }

            if (selectKP) {
                selectKP = false;
                vector<Eigen::Vector3d> vPoints;
                std::vector<SD_SLAM::MapPoint *> vNotNullMPs;
                FindMP(currentFrame.mvpMapPoints,vNotNullMPs);
                vPoints.reserve(vNotNullMPs.size());
                for (size_t i = 0; i < vNotNullMPs.size(); i++) {
                    SD_SLAM::MapPoint *pMP = vNotNullMPs[i];
                    Eigen::Vector3d worldPos = pMP->GetWorldPos();

                    //La posicion de puntos no la rotamos, ya que si se lo aplicamos estariamos haciendo
                    //una doble rotacion: rotamos 1 vez por X 180º las coordenadas y luego ademas
                    //180º todos los puntos.
                    //Por ello utilizamos las coordenadas sin rectificar ya que para las coordenadas
                    //estos se encuentran en el mismo sitio.
                    Eigen::Vector3d cameraPos = mTcw.block<3, 3>(0, 0)*worldPos + mTcw.block<3, 1>(0, 3);
                    vPoints.push_back(cameraPos);
                }

                if (!DetectPlane(vPoints, result)) {
                    selectKP = true;
                }else{
                    plane.at<float>(0,0) = result.at<float>(0,0);
                    plane.at<float>(0,1) = result.at<float>(0,1);
                    plane.at<float>(0,2) = result.at<float>(0,2);
                    plane.at<float>(0,3) = result.at<float>(0,3);
                }

                /*
                std::vector<SD_SLAM::MapPoint *> vCurrentMP = currentFrame.mvpMapPoints;
                std::vector<cv::KeyPoint> vCurrentKeys = currentFrame.mvKeys;
                float response = 0;
                for (unsigned int i = 0; i < vCurrentKeys.size(); i++) {
                    if (vCurrentKeys[i].response > response) {
                        response = vCurrentKeys[i].response;

                    }

                    if (vCurrentMP[i] != nullptr) {
                        CurrentMP = vCurrentMP[i];
                    }
                    else{
                        selectKP = true;
                    }
                    */
                }
                circle(img, cv::Point(50,50),5, cv::Scalar(0,0,255),CV_FILLED, 8,0);

            /*
            if (worldPosHighResponse(0) == 0.0 && worldPosHighResponse(1) == 0.0 && worldPosHighResponse(2) == 0.0){
                selectKP = false;
            }else{
                worldPosHighResponse = tempVect;
            }
             */
            }
            if (trackerStatus == SD_SLAM::Tracking::LOST) {
                circle(img, cv::Point(50,50),5, cv::Scalar(255,0,0),CV_FILLED, 8,0);
            }
            //std::vector<float> a = currentFrame.mvScaleFactors;



/*
            cv::Mat bitWiseXor;
            int bestDist = 256;
            int dist = 0;

            for (unsigned int i=0; i<OrbDescriptors.rows; i++) {
                dist = SD_SLAM::ORBmatcher::DescriptorDistance(OrbDescriptors.row(i),OrbSelected);
                if (dist<bestDist){
                    bestDist = dist;
                    selectKP = i;
                }
            }
            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "The value of 1 + 1 is %d", 1+1);

            int tracked = 0;
            if (currentFrame.mvpMapPoints[indexKeyPoint] && !currentFrame.mvbOutlier[indexKeyPoint]) {
                cv::circle(img, vCurrentKeys[indexKeyPoint].pt, 3, cv::Scalar(255, 0, 0), 2);
                tracked++;

            }
            */



    }

    void SDSLAMHandler::FindMP(const std::vector<SD_SLAM::MapPoint *> &vMPs, std::vector<SD_SLAM::MapPoint *> &result) {
        result = * new std::vector<SD_SLAM::MapPoint *>();
        for (size_t i = 0; i < vMPs.size(); i++) {
            SD_SLAM::MapPoint *pMP = vMPs[i];
            if (pMP != NULL){
                result.push_back(pMP);
            }
        }

    }


    bool SDSLAMHandler::DetectPlane(const vector<Eigen::Vector3d> vPoints,
                                    cv::Mat &result) {

        const int N = vPoints.size();
        int iterations = 10;

        if (N < 50)
            return false;

        cv::Mat vt;
        // Indices for minimum set selection
        vector<size_t> vAllIndices;
        vAllIndices.reserve(N);
        vector<size_t> vAvailableIndices;
        float bestDist = 1e10;
        for (int i = 0; i < N; i++)
            vAllIndices.push_back(i);

       // float bestDist = 1e10;
        //vector<float> bestvDist;

        // RANSAC
        for(int n=0; n<iterations; n++) {
            vAvailableIndices = vAllIndices;

            cv::Mat A(3, 4, CV_32F);
            A.col(3) = cv::Mat::ones(3, 1, CV_32F);

            // Get min set of points
            for (short i = 0; i < 3; ++i) {
                int randi = rand() % (vAvailableIndices.size() - 1);
                int idx = vAvailableIndices[randi];

                A.at<float>(i, 0) = vPoints[idx](0);
                A.at<float>(i, 1) = vPoints[idx](1);
                A.at<float>(i, 2) = vPoints[idx](2);

                float h = vPoints[idx](2);


                vAvailableIndices[randi] = vAvailableIndices.back();
                vAvailableIndices.pop_back();
            }

            cv::Mat u, w;
            cv::SVDecomp(A, w, u, vt, cv::SVD::MODIFY_A | cv::SVD::FULL_UV);


            float a = vt.at<float>(3, 0);
            float b = vt.at<float>(3, 1);
            float c = vt.at<float>(3, 2);
            float d = vt.at<float>(3, 3);

            vector<float> vDistances(N, 0);

            const float f = 1.0f / sqrt(a * a + b * b + c * c + d * d);

            for (int i = 0; i < N; i++)
                vDistances[i] =
                        fabs(vPoints[i](0) * a + vPoints[i](1) * b + vPoints[i](2) * c + d) * f;

            vector<float> vSorted = vDistances;
            sort(vSorted.begin(), vSorted.end());

            int nth = std::max((int) (0.2 * N), 20);
            const float medianDist = vSorted[nth];

            if (medianDist < bestDist) {
                bestDist = medianDist;
                result.at<float>(0, 0) = a;
                result.at<float>(0, 1) = b;
                result.at<float>(0, 2) = c;
                result.at<float>(0, 3) = d;
            }
        }


        return true;

    }
}
