#include "SDSLAMHandler.h"
#include <jni.h>
#include <sstream>
#include <opencv2/imgproc.hpp>

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
    jstring JNICALL Java_aopencvc_utils_SLAMHandler_TrackFrame(JNIEnv *env, jobject instance, jlong slam, jint param, jlong img) {
        std::stringstream ss;
        ss << "Hello from C++: ";

        try {
            cv::Mat * cimg = (cv::Mat*) img;
            ss << ((SLAM::SDSLAMHandler*)slam)->TrackMonocular(*cimg);
        } catch (...) {
            jclass je = env->FindClass("java/lang/Exception");
            env->ThrowNew(je, "Unknown exception in JNI code of TrackFrame()");
            return 0;
        }

        return env->NewStringUTF(ss.str().c_str());
    }
}

using std::string;

namespace SLAM {

    SDSLAMHandler::SDSLAMHandler() {
        counter_ = 0;

        // Set camera config
        SD_SLAM::Config &config = SD_SLAM::Config::GetInstance();
        config.SetCameraIntrinsics(853, 480, 400, 400, 426, 240);

        slam = new SD_SLAM::System(SD_SLAM::System::MONOCULAR);
    }

    int SDSLAMHandler::TrackMonocular(cv::Mat &img) {
        // Convert to gray to track
        cv::Mat gray;
        cv::cvtColor(img, gray, cv::COLOR_RGB2GRAY);
        string asd = "";
        slam->TrackMonocular(gray);

        // Draw in image
        DrawFrame(slam, img);

        counter_++;
        return img.cols*1000 + img.rows*1000000 + counter_;
        //return (int) pose(0,3);
    }

    void SDSLAMHandler::DrawFrame(SD_SLAM::System * slam, cv::Mat &img) {
        SD_SLAM::Tracking * tracker = slam->GetTracker();

        // Not initialized
        if (tracker->GetLastState() == SD_SLAM::Tracking::NOT_INITIALIZED) {
            std::vector<int> vMatches = tracker->GetInitialMatches();
            std::vector<cv::KeyPoint> vIniKeys = tracker->GetInitialFrame().mvKeys;
            std::vector<cv::KeyPoint> vCurrentKeys = tracker->GetCurrentFrame().mvKeys;

            for(unsigned int i=0; i<vMatches.size(); i++) {
                if (vMatches[i]>=0)
                    cv::line(img, vIniKeys[i].pt, vCurrentKeys[vMatches[i]].pt, cv::Scalar(0,255,0), 2);
            }
        }

        // Status OK
        if (tracker->GetLastState() == SD_SLAM::Tracking::OK) {
            SD_SLAM::Frame &currentFrame = tracker->GetCurrentFrame();
            std::vector<cv::KeyPoint> vCurrentKeys = currentFrame.mvKeys;

            int tracked = 0;
            for (unsigned int i=0; i<vCurrentKeys.size(); i++) {
                if (currentFrame.mvpMapPoints[i] && !currentFrame.mvbOutlier[i]) {
                    cv::circle(img, vCurrentKeys[i].pt, 3, cv::Scalar(0, 255, 0), 2);
                    tracked++;
                }
            }
        }
    }
}