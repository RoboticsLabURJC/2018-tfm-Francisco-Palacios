package aopencvc.utils;

import android.os.Environment;

import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;

public class SLAMHandler {

    private long nativeSLAM = 0;

    public SLAMHandler() {
        this.nativeSLAM = CreateSLAM();
    }


    public String TrackFrame(Mat image, Mat cameraRotation, Mat planeEq, Mat worldPosPoint) {

        String a = this.TrackFrame(this.nativeSLAM, 33, image.getNativeObjAddr(),
                cameraRotation.getNativeObjAddr(), planeEq.getNativeObjAddr(),
                worldPosPoint.getNativeObjAddr());
        //System.out.println("Punto param z: " + planeEq.toString());

        return a;
    }


    public void SaveTraj(){
        String filepath = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOWNLOADS) + "/trajectory.yaml";

        File traj = new File(filepath);
        if (traj.exists()){
            traj.delete();
        }
        /*
        try {
            traj.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        SaveTrajectory(filepath, this.nativeSLAM);
    }


    // Native methods implemented by SLAM native library.

    private native void SaveTrajectory(String filepath, long slam);

    private native long CreateSLAM();
    private native String TrackFrame(long slam, int param, long img, long rotation,
                                    long planeEq, long wPPoint);

}
