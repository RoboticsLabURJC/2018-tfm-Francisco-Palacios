package com.example.aopencvc.androidopencvc;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Recorder extends IntentService {

    private MediaRecorder recorder;
    private MediaProjection projection;
    private MediaProjectionManager projection_manager;
    private VirtualDisplay virtual_display;
    private int density;


    public Recorder() {
        super("Recorder");
    }


    public void onCreate(){
        super.onCreate();

        projection_manager = (MediaProjectionManager) getSystemService
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
            startActivity(projection_manager.createScreenCaptureIntent());

        }
        virtual_display = createVirtualDisplay();
        recorder.start();
    }



    private VirtualDisplay createVirtualDisplay() {
        return projection.createVirtualDisplay("ARCamera",
                1280, 720, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                recorder.getSurface(), null, null);
    }



    public void onDestroy(){
        super.onDestroy();
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (projection != null) {
            projection.stop();
            projection = null;
        }
        virtual_display.release();
        virtual_display = null;
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            density = intent.getExtras().getInt("density");

        }
    }


}
