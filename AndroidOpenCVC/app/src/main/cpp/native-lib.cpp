#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>

using namespace std;
using namespace cv;

extern "C"
{
void JNICALL Java_com_example_aopencvc_androidopencvc_MainActivity_salt(JNIEnv *env, jobject instance,
                                                                      jlong matAddrGray) {
    Mat &mGr = *(Mat *) matAddrGray;

    Canny( mGr, mGr, 20, 60, 3 );
}
}