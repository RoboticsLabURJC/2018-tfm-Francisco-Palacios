package aopencvc.utils;

import org.opencv.core.Mat;

public class SLAMHandler {

    private long nativeSLAM = 0;

    public SLAMHandler() {
        this.nativeSLAM = CreateSLAM();
    }


    public String TrackFrame(Mat image, Mat vKeyFramesPos, Mat planeEq, Mat worldPosPoint) {
        String a = this.TrackFrame(this.nativeSLAM, 33, image.getNativeObjAddr(),
                vKeyFramesPos.getNativeObjAddr(), planeEq.getNativeObjAddr(),
                worldPosPoint.getNativeObjAddr());

        return a;
    }




    // Native methods implemented by SLAM native library.

    public native void getKeyFramePositions(long slam, long keyFramePos);
    private native long CreateSLAM();
    public native String TrackFrame(long slam, int param, long img, long vKFPs,
                                    long planeEq, long wPPoint);

}
