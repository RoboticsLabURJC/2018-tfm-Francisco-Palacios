package com.example.aopencvc.androidopencvc;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;

import aopencvc.utils.ActivitySingleton;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Recorder {

    private MediaRecorder recorder;
    private MediaProjection projection;
    private MediaProjectionManager projection_manager;
    private VirtualDisplay virtual_display;
    private Activity mainActivity;
    private int density;
    private int REQUEST_CODE = 1000;


    public Recorder(Activity _mainActivity){
        mainActivity = _mainActivity;
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE}, 10);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.densityDpi;
        projection_manager = (MediaProjectionManager) mainActivity.getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
        recorder = new MediaRecorder();

        File video = new File(Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS) + "/video.mp4");
        if (video.exists()){
            video.delete();
        }
        initRecorder();
        shareScreen();
    }

    private void initRecorder(){
        try {

            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video.mp4");
            recorder.setVideoSize(1280, 720);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setVideoEncodingBitRate(512 * 2000);
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
            mainActivity.startActivity(projection_manager.createScreenCaptureIntent());
            mainActivity.startActivityForResult(projection_manager.createScreenCaptureIntent(),REQUEST_CODE);
        }
    }

    public void startRecorder(int resultCode, Intent data){
        projection = projection_manager.getMediaProjection(resultCode, data);
        virtual_display = createVirtualDisplay();
        recorder.start();
    }



    private VirtualDisplay createVirtualDisplay() {
        return projection.createVirtualDisplay("ARCamera",
                1280, 720, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(), null, null);
    }



    public void onDestroy(Context context){
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (projection != null) {
            projection.stop();
            projection = null;
        }
        if (virtual_display != null) {
            virtual_display.release();
            virtual_display = null;
        }
        MediaScannerConnection.scanFile(context,new String[]{Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS) + "/video.mp4"},null,new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });
    }



}
