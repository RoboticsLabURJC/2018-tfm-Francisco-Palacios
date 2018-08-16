package aopencvc.opengl;

public class OpenGLJNIWrapper {

    static {
        System.loadLibrary("opengl_example");
    }

    public static native void on_surface_created();

    public static native void on_surface_changed(int width, int height);

    public static native void on_draw_frame();
}
