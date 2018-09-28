//
// Created by fjpalfer on 23/08/18.
//

#ifndef ANDROIDOPENCVC_SDSLAMHANDLER_H
#define ANDROIDOPENCVC_SDSLAMHANDLER_H

#include <string>
#include <opencv2/core/core.hpp>
#include <Eigen/Dense>
#include <System.h>
#include <Config.h>
#include <android/log.h>
#include <ORBmatcher.h>


namespace SLAM{
    class SDSLAMHandler {
    public:

            SDSLAMHandler();


            int TrackMonocular(cv::Mat &img, cv::Mat &rotation,
                               cv::Mat &mOw, cv::Mat &wPPoint);

            SD_SLAM::MapPoint * getMapPoints();

            SD_SLAM::Tracking::eTrackingState getTrackingStatus();




    private:

        void DrawFrame(SD_SLAM::System * slam, cv::Mat &img, cv::Mat &rotation,
                       cv::Mat &mOw, cv::Mat &wPPoint);

        SD_SLAM::Tracking::eTrackingState trackerStatus;

        bool selectKP;

        Eigen::Vector3d worldPosHighResponse;

        int counter_;

        SD_SLAM::MapPoint * CurrentMP;


        SD_SLAM::System * slam;

    };
}



#endif //ANDROIDOPENCVC_SDSLAMHANDLER_H
