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


namespace SLAM{
    class SDSLAMHandler {
    public:

            SDSLAMHandler();


            int TrackMonocular(cv::Mat &img);


    private:

        void DrawFrame(SD_SLAM::System * slam, cv::Mat &img);

        int counter_;

        SD_SLAM::System * slam;

    };
}



#endif //ANDROIDOPENCVC_SDSLAMHANDLER_H
