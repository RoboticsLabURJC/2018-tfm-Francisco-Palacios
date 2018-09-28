#include "SDSLAMHandler.h"
#include <jni.h>
#include <sstream>
#include <opencv2/imgproc.hpp>

#define APPNAME "AR_SD_SLAM"

class System;

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
                                                                jlong cameraRotation, jlong cameraTranslation,
                                                               jlong worldPosPoint) {
        std::stringstream ss;
        ss << "Hello from C++: ";

        try {
            cv::Mat * cimg = (cv::Mat*) img;
            cv::Mat * cRot = (cv::Mat*) cameraRotation;
            cv::Mat * cTra = (cv::Mat*) cameraTranslation;
            cv::Mat * wPPoint = (cv::Mat*) worldPosPoint;
            ss << ((SLAM::SDSLAMHandler*)slam)->TrackMonocular(*cimg, *cRot, *cTra, *wPPoint);
        } catch (...) {
            jclass je = env->FindClass("java/lang/Exception");
            env->ThrowNew(je, "Unknown exception in JNI code of TrackFrame()");
            return 0;
        }

        return env->NewStringUTF(ss.str().c_str());
    }
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_aopencvc_utils_SLAMHandler_GetPointWorldPos(JNIEnv *env, jobject instance, jlong slam,
                                                 jlong worldPosPoint) {
    bool gotPoint = false;
    if( ((SLAM::SDSLAMHandler*)slam)->getTrackingStatus() == SD_SLAM::Tracking::OK){
        cv::Mat * point = (cv::Mat*) worldPosPoint;

        SD_SLAM::MapPoint * currentMP = ((SLAM::SDSLAMHandler*)slam)->getMapPoints();
        Eigen::Vector3d pointWorldPos = currentMP->GetWorldPos();

        point->at<double>(0) = pointWorldPos(0);
        point->at<double>(1) = pointWorldPos(1);
        point->at<double>(2) = pointWorldPos(2);

        gotPoint = true;
    }

    return gotPoint;

}




namespace SLAM {

    SDSLAMHandler::SDSLAMHandler() {
        counter_ = 0;


        // Set camera config
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();
        config.SetCameraIntrinsics(1280, 720, 1080.33874061861, 1081.398961189691,
                                   644.2552668090187, 347.007496696664);
        config.SetCameraDistortion(0.1390849622709781, -0.9989370047449903,
                                   0.0009328385593714286, 0.0000381832961871823, 2.231184493469707);
        selectKP = true;

        slam = new SD_SLAM::System(SD_SLAM::System::MONOCULAR);
    }

    int SDSLAMHandler::TrackMonocular(cv::Mat &img, cv::Mat &rotation,
                                      cv::Mat &mOw, cv::Mat &wPPoint) {
        // Convert to gray to track
        cv::Mat gray;
        cv::cvtColor(img, gray, cv::COLOR_RGB2GRAY);
        slam->TrackMonocular(gray);
        // Draw in image
        DrawFrame(slam, img, rotation, mOw, wPPoint);

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

    void SDSLAMHandler::DrawFrame(SD_SLAM::System * slam, cv::Mat &img, cv::Mat &rotation,
                                  cv::Mat &mOw, cv::Mat &wPPoint) {
        SD_SLAM::Tracking * tracker = slam->GetTracker();
        trackerStatus = tracker->GetLastState();
        // Not initialized
        if (trackerStatus == SD_SLAM::Tracking::NOT_INITIALIZED) {
            std::vector<int> vMatches = tracker->GetInitialMatches();
            std::vector<cv::KeyPoint> vIniKeys = tracker->GetInitialFrame().mvKeys;
            std::vector<cv::KeyPoint> vCurrentKeys = tracker->GetCurrentFrame().mvKeys;

            for(unsigned int i=0; i<vMatches.size(); i++) {
                if (vMatches[i]>=0)
                    cv::line(img, vIniKeys[i].pt, vCurrentKeys[vMatches[i]].pt, cv::Scalar(0,255,0), 2);
            }
        }

        // Status OK
        if (trackerStatus == SD_SLAM::Tracking::OK) {
            SD_SLAM::Frame &currentFrame = tracker->GetCurrentFrame();
            std::vector<cv::KeyPoint> vCurrentKeys = currentFrame.mvKeys;
            //std::vector<SD_SLAM::MapPoint *> vCurrentMP = currentFrame.mvpMapPoints;


            Eigen::Matrix3d mRwc = currentFrame.GetRotationInverse();
            Eigen::Vector3d eigenCen = currentFrame.GetCameraCenter();

            mOw.at<double>(0) = eigenCen(0);
            mOw.at<double>(1) = eigenCen(1);
            mOw.at<double>(2) = eigenCen(2);
            Eigen::AngleAxisd eigenRot;
            eigenRot.fromRotationMatrix(mRwc);
            // char matRow[100] = ""; // You can calculate how much memory u need, but for debug prints just put a big enough number

            //for (int i = 0; i < 3; i++) {
            //    sprintf(matRow + strlen(matRow), "%lf ", mOw(i)); // You can use data from row you need by accessing trainingLabels.row(...).data
            //}

            //__android_log_print(ANDROID_LOG_ERROR, "SEARCH FOR THIS TAG", "%s", matRow);
            cv::Mat OrbDescriptors = currentFrame.mDescriptors;

            double rot_angle = eigenRot.angle();
            double xRot = (eigenRot.angle() * 57.2958) * eigenRot.axis()(0);
            double yRot = (eigenRot.angle() * 57.2958) * eigenRot.axis()(1);
            double zRot = (eigenRot.angle() * 57.2958) * eigenRot.axis()(2);

            rotation.at<double>(0) = xRot;
            rotation.at<double>(1) = yRot;
            rotation.at<double>(2) = zRot;

            if (selectKP) {
                selectKP = false;
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
                }
                /*
                if (worldPosHighResponse(0) == 0.0 && worldPosHighResponse(1) == 0.0 && worldPosHighResponse(2) == 0.0){
                    selectKP = false;
                }else{
                    worldPosHighResponse = tempVect;
                }
                 */
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

    }
}
