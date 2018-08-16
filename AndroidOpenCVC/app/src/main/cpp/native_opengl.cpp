#include <jni.h>
#include <opengl_example.h>

using namespace std;

extern "C"
JNIEXPORT void JNICALL
Java_aopencvc_opengl_OpenGLJNIWrapper_on_1draw_1frame(JNIEnv *env, jclass type) {

    on_draw_frame();

}



extern "C"
JNIEXPORT void JNICALL
Java_aopencvc_opengl_OpenGLJNIWrapper_on_1surface_1changed(JNIEnv *env, jclass type, jint width,
                                                           jint height) {

    on_surface_changed();

}

extern "C"
JNIEXPORT void JNICALL
Java_aopencvc_opengl_OpenGLJNIWrapper_on_1surface_1created(JNIEnv *env, jclass type) {

    on_surface_created();

}