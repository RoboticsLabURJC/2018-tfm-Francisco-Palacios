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
                                                                jlong vKeyFramesPos, jlong planeEq,
                                                               jlong cameraPose) {
        std::stringstream ss;
        ss << "Hello from C++: ";

        try {
            cv::Mat * cimg = (cv::Mat*) img;
            cv::Mat * vKFPs = (cv::Mat*) vKeyFramesPos;
            cv::Mat * plane = (cv::Mat*) planeEq;
            cv::Mat * pose = (cv::Mat*) cameraPose;
            ss << ((SLAM::SDSLAMHandler*)slam)->TrackMonocular(*cimg, *vKFPs, *plane, *pose);
        } catch (...) {
            jclass je = env->FindClass("java/lang/Exception");
            env->ThrowNew(je, "Unknown exception in JNI code of TrackFrame()");
            return 0;
        }

        return env->NewStringUTF(ss.str().c_str());
    }
}


extern "C"
std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_aopencvc_utils_SLAMHandler_SaveTrajectory(JNIEnv *env, jobject instance, jstring filepath_, jlong slam) {
    //const char *filepath = env->GetStringUTFChars(filepath_, 0);
    std::string filePathStd = jstring2string(env,filepath_);

    ((SLAM::SDSLAMHandler*)slam)->SaveTrajectory(filePathStd);
    //env->ReleaseStringUTFChars(filepath_, filepath);
}



using std::vector;
using namespace std;

namespace SLAM {

    SDSLAMHandler::SDSLAMHandler() {
        counter_ = 0;
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();

/*
        // Set camera config
        config.SetCameraIntrinsics(640, 360, 673.861075, 677.584410,
                                   315.8385, 189.6916);
        config.SetCameraDistortion(-0.350240, 1.384144,
                                   -0.008788, -0.022544, -2.385009);
*/

        config.SetCameraIntrinsics(640, 360, 525.4634783, 525.529018,
                                   318.754478, 176.578987);
        config.SetCameraDistortion(0.4089838, -2.745104,
                                   -0.0103676, 0.004119270, 12.861473);

        config.SetUsePattern(true);
        selectKP = true;
        slam = new SD_SLAM::System(SD_SLAM::System::MONOCULAR);
    }

    int SDSLAMHandler::TrackMonocular(cv::Mat &img, cv::Mat &KFpos,
                                      cv::Mat &plane, cv::Mat &pose) {
        // Convert to gray to track
        cv::Mat gray;
        cv::cvtColor(img, gray, cv::COLOR_RGB2GRAY);
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();


        slam->TrackMonocular(gray);
        tracker = slam->GetTracker();
        trackerStatus = tracker->GetLastState();
        // Draw in image
        GetPose(pose);
        GetPlaneEquation(plane, pose);
        GetKFPositions(KFpos);
        DrawFrame(img);

        counter_++;
        return img.cols*1000 + img.rows*1000000 + counter_;
    }

    void SDSLAMHandler::GetKFPositions(cv::Mat &KFPos){
        std::vector<SD_SLAM::KeyFrame *> vKF = slam->GetMap()->GetAllKeyFrames();
        sort(vKF.begin(), vKF.end(), SD_SLAM::KeyFrame::lId);
        KFPos = *new cv::Mat(vKF.size(), 4, CV_32F);
        for (int i = 0; i< vKF.size();i++){
            SD_SLAM::KeyFrame * KF = vKF.at(i);
            Matrix4d KFpose = KF->GetPose();
            KFPos.at<float>(i,0) = (float) KFpose(0,3);
            KFPos.at<float>(i,1) = -(float) KFpose(1,3);
            KFPos.at<float>(i,2) = -(float) KFpose(2,3);
        }
    }



    void SDSLAMHandler::GetPose(cv::Mat &pose){
        SD_SLAM::Frame currentFrame = tracker->GetCurrentFrame();
        Eigen::Matrix4d mTwc = currentFrame.GetPoseInverse();
        numberOfKeyPoints = currentFrame.N;

        /*
        Eigen::Matrix3d mRcw = mTcw.block<3, 3>(0, 0);
        Eigen::Vector3d mtcw = mTcw.block<3, 1>(0, 3);
        Eigen::Matrix3d rotMat;
        rotMat.setZero();
        rotMat(0,0) = 1;
        rotMat(1,1) = -1;
        rotMat(2,2) = -1;
        Eigen::Matrix3d mRcwRotated = rotMat * mRcw;
        Eigen::Vector3d mtcwRotated = rotMat * mtcw;

        pose = *new cv::Mat(1, 4, CV_64F);


        Eigen::Matrix4d newPose;
        newPose.setZero();
        newPose.block<3, 3>(0, 0) = mRcwRotated;
        newPose.block<3, 1>(0, 3) = mtcwRotated;
        newPose.block<1,4>(3,0) = mTcw.block<1,4>(3,0);
         */

/*
        Eigen::Quaterniond q(mTwc.block<3, 3>(0, 0));
        q = *new Eigen::Quaterniond(q.w(),-q.x(),q.y(),q.z());
        mTwc.block<3, 3>(0, 0) = q.toRotationMatrix();
        mTwc(0,3) = -mTwc(0,3);
*/
        for (int i = 0;i<pose.rows * pose.cols;i++){
            pose.at<double>(i%4,i/4) = mTwc(i);
        }
    }

    void SDSLAMHandler::GetPlaneEquation(cv::Mat &planeEq, cv::Mat pose){
        if(selectKP) {
            SD_SLAM::Frame currentFrame = tracker->GetCurrentFrame();
            vector<Eigen::Vector3d> vPoints;
            std::vector<SD_SLAM::MapPoint *> vNotNullMPs;
            FindMP(currentFrame.mvpMapPoints, vNotNullMPs);
            vPoints.reserve(vNotNullMPs.size());
            Eigen::Matrix4d mTcw = tracker->GetCurrentFrame().GetPose();
            for (size_t i = 0; i < vNotNullMPs.size(); i++) {
                SD_SLAM::MapPoint *pMP = vNotNullMPs[i];
                Eigen::Vector3d worldPos = pMP->GetWorldPos();

                //La posicion de puntos no la rotamos, ya que si se lo aplicamos estariamos haciendo
                //una doble rotacion: rotamos 1 vez por X 180º las coordenadas y luego ademas
                //180º todos los puntos.
                //Por ello utilizamos las coordenadas sin rectificar ya que para las coordenadas
                //estos se encuentran en el mismo sitio.

                /// SEGURO??? quizas si que haya que rotar los puntos.
                // rotacion de los puntos. Esto lo he puesto en una de las ultimas actus y no estaba
                cv::Range colRange = cv::Range(0, pose.cols - 1);

                Eigen::Matrix3d mRcw = mTcw.block<3, 3>(0, 0);
                Eigen::Vector3d mtcw = mTcw.block<3, 1>(0, 3);

                Eigen::Vector3d cameraPos = mRcw * worldPos + mtcw;
                //cameraPos = rotMat * cameraPos;

                vPoints.push_back(cameraPos);
            }

            cv::Mat result = *new cv::Mat(1, 4, CV_64F);
            if (!DetectPlane(vPoints, result)) {
                selectKP = true;
            } else {
                planeEq.at<float>(0, 0) = result.at<float>(0, 0);
                planeEq.at<float>(0, 1) = result.at<float>(0, 1);
                planeEq.at<float>(0, 2) = result.at<float>(0, 2);
                planeEq.at<float>(0, 3) = result.at<float>(0, 3);
                selectKP = false;
            }
        }

    }


    void SDSLAMHandler::DrawFrame(cv::Mat &img) {

        // Not initialized

        const char *N = ToString(numberOfKeyPoints).c_str();
        if (trackerStatus == SD_SLAM::Tracking::NOT_INITIALIZED) {
            putText(img,N,cv::Point(50,50),CV_FONT_HERSHEY_SIMPLEX,1,cv::Scalar(0,255,0),1.5);
        }
        // Status OK
        if (trackerStatus == SD_SLAM::Tracking::OK) {
            putText(img,N,cv::Point(50,50),CV_FONT_HERSHEY_SIMPLEX,1,cv::Scalar(0,0,255),1.5);
        }
        if (trackerStatus == SD_SLAM::Tracking::LOST) {
            putText(img,N,cv::Point(50,50),CV_FONT_HERSHEY_SIMPLEX,1,cv::Scalar(255,0,0),1.5);
        }


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
                vDistances[i] = fabs(vPoints[i](0) * a + vPoints[i](1) * b + vPoints[i](2) * c + d) * f;

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



    void SDSLAMHandler::SaveTrajectory(std::string filePath) {
        int counter;
        std::string output = "%YAML:1.0\n";

        std::cout << "Saving trajectory to " << filePath << " ..." << std::endl;
        SD_SLAM::Map * mpMap = slam->GetMap();
        vector<SD_SLAM::KeyFrame*> vpKFs = mpMap->GetAllKeyFrames();
        sort(vpKFs.begin(), vpKFs.end(), SD_SLAM::KeyFrame::lId);

        std::ofstream f;
        f.open(filePath.c_str());

        // Save camera parameters
        output += "camera:\n";
        output += "  fx: " + ToString(SD_SLAM::Config::fx()) + "\n";
        output += "  fy: " + ToString(SD_SLAM::Config::fy()) + "\n";
        output += "  cx: " + ToString(SD_SLAM::Config::cx()) + "\n";
        output += "  cy: " + ToString(SD_SLAM::Config::cy()) + "\n";
        output += "  k1: " + ToString(SD_SLAM::Config::k1()) + "\n";
        output += "  k2: " + ToString(SD_SLAM::Config::k2()) + "\n";
        output += "  p1: " + ToString(SD_SLAM::Config::p1()) + "\n";
        output += "  p2: " + ToString(SD_SLAM::Config::p2()) + "\n";
        output += "  k3: " + ToString(SD_SLAM::Config::k3()) + "\n";

        // Save keyframes
        output += "keyframes:\n";

        for(size_t i=0; i<vpKFs.size(); i++) {
            SD_SLAM::KeyFrame* pKF = vpKFs[i];

            if(pKF->isBad())
                continue;

            Eigen::Matrix4d pose = pKF->GetPoseInverse();
            Eigen::Quaterniond q(pose.block<3, 3>(0, 0));
            Eigen::Vector3d t = pose.block<3, 1>(0, 3);
            output += "  - id: " + ToString(pKF->mnId) + "\n";
           // output += "    filename: \"" + pKF->mFilename + "\"\n";
            output += "    pose:\n";
            output += "      - " + ToString(q.w()) + "\n";
            output += "      - " + ToString(q.x()) + "\n";
            output += "      - " + ToString(q.y()) + "\n";
            output += "      - " + ToString(q.z()) + "\n";
            output += "      - " + ToString(t(0)) + "\n";
            output += "      - " + ToString(t(1)) + "\n";
            output += "      - " + ToString(t(2)) + "\n";
        }

        // Save map points
        output += "points:\n";
        counter = 0;

        const vector<SD_SLAM::MapPoint*> &vpMPs = mpMap->GetAllMapPoints();
        for (size_t i = 0, iend=vpMPs.size(); i < iend; i++) {
            if (vpMPs[i]->isBad())
                continue;
            Eigen::Vector3d pos = vpMPs[i]->GetWorldPos();

            output += "  - id: " +ToString(counter) + "\n";
            output += "    pose:\n";
            output += "      - " + ToString(pos(0)) + "\n";
            output += "      - " + ToString(pos(1)) + "\n";
            output += "      - " + ToString(pos(2)) + "\n";
            output += "    observations:\n";

            // Observations
            std::map<SD_SLAM::KeyFrame*, size_t> observations = vpMPs[i]->GetObservations();

            for (std::map<SD_SLAM::KeyFrame*, size_t>::iterator mit=observations.begin(), mend=observations.end(); mit != mend; mit++) {
                SD_SLAM::KeyFrame* kf = mit->first;
                const cv::KeyPoint &kp = kf->mvKeys[mit->second];

                output += "      - kf: " + ToString(kf->mnId) + "\n";
                output += "        pixel:\n";
                output += "          - "+ ToString(kp.pt.x) + "\n";
                output += "          - "+ ToString(kp.pt.y) + "\n";
            }

            counter++;
        }

        f << output;
        f.close();
        std::cout << "Trajectory saved!" << std::endl;
    }

    template <typename T>
    std::string SDSLAMHandler::ToString(T value)
    {
        std::ostringstream os ;
        os << value ;
        return os.str() ;
    }
}
