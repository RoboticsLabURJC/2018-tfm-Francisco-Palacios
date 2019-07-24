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
#include <PatternDetector.h>

using std::vector;


namespace SLAM{
    class SDSLAMHandler {
    public:

            SDSLAMHandler();


            int TrackMonocular(cv::Mat &img, cv::Mat &KFpos,
                               cv::Mat &mOw, cv::Mat &pose);

            SD_SLAM::MapPoint * getMapPoints();

            SD_SLAM::Tracking::eTrackingState getTrackingStatus();

            void SaveTrajectory(std::string filePath);





    private:

        void DrawFrame(cv::Mat &img);

        bool DetectPlane(const vector<Eigen::Vector3d> vPoints, cv::Mat &result);

        void FindMP(const std::vector<SD_SLAM::MapPoint*> &vMPs,std::vector<SD_SLAM::MapPoint *> &result);

        void GetPose(cv::Mat &pose);

        void GetKFPositions(cv::Mat &KFPos);

        void GetPlaneEquation(cv::Mat &planeEq, cv::Mat pose);

        SD_SLAM::Tracking::eTrackingState trackerStatus;

        template <typename T>
        std::string ToString(T value);

        bool selectKP;

        int counter_;

        SD_SLAM::Tracking * tracker;


        SD_SLAM::MapPoint * CurrentMP;

        int numberOfKeyPoints;


        SD_SLAM::System * slam;

    };
}



#endif //ANDROIDOPENCVC_SDSLAMHANDLER_H
