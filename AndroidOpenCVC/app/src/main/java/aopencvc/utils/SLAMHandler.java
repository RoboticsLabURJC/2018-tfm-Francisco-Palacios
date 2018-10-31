package aopencvc.utils;

import org.opencv.core.Mat;

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


    // Native methods implemented by SLAM native library.


    private native long CreateSLAM();
    public native String TrackFrame(long slam, int param, long img, long rotation,
                                    long planeEq, long wPPoint);

}
