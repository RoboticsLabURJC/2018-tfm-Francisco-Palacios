package aopencvc.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class SurfaceViewer extends GLSurfaceView {


    public SurfaceViewer(Context context){
        super(context);
        init();
    }

    public SurfaceViewer(Context context, AttributeSet attr){
        super(context);
        init();
    }

    public void init(){
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setRenderer(new ObjectRenderer());

    }
}