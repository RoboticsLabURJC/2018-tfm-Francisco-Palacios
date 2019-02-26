package com.example.aopencvc.androidopencvc;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;

import aopencvc.opengl.SurfaceViewer;
import aopencvc.utils.ActivitySingleton;
import aopencvc.utils.CameraHandler;
import aopencvc.utils.SLAMHandler;


public class ARCamera extends AppCompatActivity{


    private SurfaceViewer mGLView;
    private FrameLayout mFrame;
    private static final String TAG = "OCVARCamera::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private int  REQUEST_CODE = 1000;
    private Recorder recorder;
    private SLAMHandler slamHandler;



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
        Log.i(TAG, "called onCreate");
        Bundle params = getIntent().getExtras();

        boolean record;
        if (params != null){
            record = params.getBoolean("recording");

        }else{
            record = false;

        }

        if (record){
            recorder = new Recorder(this);
        }


        mGLView = new SurfaceViewer(this);
        mGLView.setZOrderOnTop(true);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        slamHandler = new SLAMHandler();
        CameraHandler cameraHandler = new CameraHandler(this.getBaseContext(), slamHandler, mGLView);


        ActivityCompat.requestPermissions(ARCamera.this,
                new String[]{Manifest.permission.CAMERA},
                1);
        setContentView(R.layout.activity_arcamera);
        mFrame = findViewById(R.id.frame_opengl_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_camera);

        mOpenCvCameraView.setZOrderOnTop(false);
        mFrame.addView(mGLView);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(cameraHandler);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {

            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }


        recorder.startRecorder(resultCode,data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mOpenCvCameraView.disableView();
        Intent intent = new Intent(this,Recorder.class);
        stopService(intent);
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
    public void onDestroy() {
        slamHandler.SaveTraj(this);
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if (recorder != null){
            recorder.onDestroy(this);
        }

        super.onDestroy();

    }
}
