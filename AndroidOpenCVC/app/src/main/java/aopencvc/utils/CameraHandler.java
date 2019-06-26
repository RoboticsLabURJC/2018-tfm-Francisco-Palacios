package aopencvc.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.sql.Timestamp;

import aopencvc.opengl.SurfaceViewer;

import org.opencv.calib3d.Calib3d; //Solve pnp
import org.opencv.imgproc.Imgproc;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_64F;

public class CameraHandler implements CameraBridgeViewBase.CvCameraViewListener2 {


    private Context context;
    private SLAMHandler slamHandler;
    private Mat vKeyFrames;
    private Mat planeEq;
    private Mat cameraPose;
    private SurfaceViewer mGLView;
    private long sumTime;
    private int countTime;
    private boolean firstIt;

    public CameraHandler(Context context, SLAMHandler slamHandler, SurfaceViewer mGLView) {
        this.context = context;
        this.slamHandler = slamHandler;
        planeEq = new Mat(1,4, CV_32F, new Scalar(0.0));
        cameraPose = new Mat(4,4, CV_64F, new Scalar(0.0));
        this.mGLView = mGLView;
        sumTime = 0;
        countTime= 0;
        firstIt = true;
    }



    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {



        ///Estamos devolviendo la imagen a su tamano natural.
        ///Esto puede provocar que algunos puntos salgan mal calculados por no ternerlos en cuenta
        ///Ahora lo voy a desactivar, pero esto estaba activo hasta el 01/03/2019, despues del
        ///Cambio del TrailCam de pose a KFs.

        Mat image = inputFrame.rgba();

        if (firstIt){
            System.out.println("Por first It");
            ExtrinsicsCalculator.SolvePnP(inputFrame.gray());
            firstIt = false;
        }

        int irows = image.rows();
        int icols = image.cols();
        System.out.println("cosa"+irows);

        int rows = 720;
        int cols = rows*icols/irows;

        Mat resized = new Mat();
        //tablet
       // Size size = new Size(640,360);
        Size size = new Size(1280,720);
        Imgproc.resize(image,resized,size);



        vKeyFrames = new Mat(1,3, CV_64F, Scalar.all(0.0));
        // Process frame
        //long startTime = System.currentTimeMillis();
        String res = slamHandler.TrackFrame(resized, vKeyFrames, planeEq, cameraPose);
        /*
        //long finalTime = System.currentTimeMillis();
        //long elapsedTime = finalTime - startTime;

       // System.out.println("Frame " + countTime + ": " + elapsedTime + " ms");
        //sumTime += elapsedTime;
        //countTime++;
        //float avgTime = sumTime/countTime;
        //System.out.println("AverageTime: " + avgTime + " ms");
        */
        Log.d("SD-SLAM", "Pose es " + res);

        mGLView.putVKeyFramesPos(vKeyFrames);
        mGLView.putPlaneEquation(planeEq);
        mGLView.putCameraPose(cameraPose);

        // Resize to original size

       // Cambiado el resize, ver comentario principio metodo.
        Size isize = new Size(icols, irows);
        Imgproc.resize(resized, image, isize);




        return image;
    }
}
