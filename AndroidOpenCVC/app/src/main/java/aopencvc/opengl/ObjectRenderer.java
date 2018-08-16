package aopencvc.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;


public class ObjectRenderer implements GLSurfaceView.Renderer {


    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        OpenGLJNIWrapper.on_draw_frame();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        OpenGLJNIWrapper.on_surface_created();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        OpenGLJNIWrapper.on_surface_changed(width,height);
    }
}