package aopencvc.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraHandler implements CameraBridgeViewBase.CvCameraViewListener2 {


    private Context context;
    private SLAMHandler slamHandler;
    private CameraBridgeViewBase mOpenCvCameraView;

    public CameraHandler(Context context, SLAMHandler slamHandler) {
        this.context = context;
        this.slamHandler = slamHandler;
    }

    public boolean checkCameraHardware() {
        if (this.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
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
        Mat image = inputFrame.rgba();
        int irows = image.rows();
        int icols = image.cols();

        int rows = 480;
        int cols = rows*icols/irows;

        Mat resized = new Mat();
        Size size = new Size(cols,rows);
        Imgproc.resize(image,resized,size);

        Log.d("SD-SLAM", "Image resized to " + Integer.toString(cols) + "x" + Integer.toString(rows));


        // Process frame
        String res = slamHandler.TrackFrame(resized);
        Log.d("SD-SLAM", "Pose es " + res);

        // Resize to original size
        Size isize = new Size(icols, irows);
        Imgproc.resize(resized, image, isize);


        return image;
    }
}
