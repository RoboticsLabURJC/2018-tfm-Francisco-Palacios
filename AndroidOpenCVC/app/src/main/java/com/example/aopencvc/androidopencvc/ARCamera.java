package com.example.aopencvc.androidopencvc;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
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
import aopencvc.utils.CameraHandler;
import aopencvc.utils.SLAMHandler;


public class ARCamera extends AppCompatActivity{


    private SurfaceViewer mGLView;
    private FrameLayout mFrame;
    private static final String TAG = "OCVARCamera::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;

    private MediaRecorder recorder;
    private MediaProjection projection;
    private MediaProjectionManager projection_manager;
    private VirtualDisplay virtual_display;
    private int density;
    private int  REQUEST_CODE = 1000;




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
            if (ContextCompat.checkSelfPermission(ARCamera.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ARCamera.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE}, 10);
            }

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            density = metrics.densityDpi;
            projection_manager = (MediaProjectionManager) getSystemService
                    (Context.MEDIA_PROJECTION_SERVICE);
            recorder = new MediaRecorder();
            initRecorder();
            shareScreen();
        }


        mGLView = new SurfaceViewer(this);
        mGLView.setZOrderOnTop(true);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        SLAMHandler slamHandler = new SLAMHandler();
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

    private void initRecorder() {
        try {

            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video.mp4");
            recorder.setVideoSize(1280, 720);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setVideoEncodingBitRate(512 * 1000);
            recorder.setVideoFrameRate(30);
            recorder.prepare();
            System.out.println("En init recorder");

            //recorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreen(){
        if (projection == null) {
            System.out.println("projection");
            startActivityForResult(projection_manager.createScreenCaptureIntent(),REQUEST_CODE);
            return;
        }
        virtual_display = createVirtualDisplay();
        recorder.start();
        System.out.println("despues start");

    }

    private VirtualDisplay createVirtualDisplay() {
        return projection.createVirtualDisplay("ARCamera",
                1280, 720, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(), null, null);
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

        projection = projection_manager.getMediaProjection(resultCode, data);
        virtual_display = createVirtualDisplay();
        recorder.start();
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
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (projection != null) {
            projection.stop();
            projection = null;
        }
        if (virtual_display != null){
            virtual_display.release();
            virtual_display = null;
        }

    }
}
