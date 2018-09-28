package aopencvc.utils;

import org.opencv.core.Mat;

public class SLAMHandler {

    private long nativeSLAM = 0;

    public SLAMHandler() {
        this.nativeSLAM = CreateSLAM();
    }


    public String TrackFrame(Mat image, Mat cameraRotation, Mat cameraTranslation, Mat worldPosPoint) {
        return this.TrackFrame(this.nativeSLAM, 33, image.getNativeObjAddr(),
                                cameraRotation.getNativeObjAddr(), cameraTranslation.getNativeObjAddr(),
                                worldPosPoint.getNativeObjAddr());
    }

    public boolean GetWorldPos(Mat pointPos){
        return this.GetPointWorldPos(this.nativeSLAM, pointPos.getNativeObjAddr());
    }

    // Native methods implemented by SLAM native library.


    private native long CreateSLAM();
    public native String TrackFrame(long slam, int param, long img, long rotation,
                                    long translation, long wPPoint);

    public native boolean GetPointWorldPos(long slam, long worldPosPoint);

}
