package com.example.aopencvc.androidopencvc;


import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import aopencvc.opengl.SurfaceViewer;


public class ARCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private SurfaceViewer mGLView;
    private FrameLayout mFrame;
    private static final String TAG = "OCVARCanera::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;




    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new SurfaceViewer(this);
        mGLView.setZOrderOnTop(true);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        Log.i(TAG, "called onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        ActivityCompat.requestPermissions(ARCamera.this,
                new String[]{Manifest.permission.CAMERA},
                1);
        setContentView(R.layout.activity_arcamera);
        mFrame = findViewById(R.id.frame_opengl_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_camera);

        mOpenCvCameraView.setZOrderOnTop(false);
        mFrame.addView(mGLView);


        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mOpenCvCameraView.disableView();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
