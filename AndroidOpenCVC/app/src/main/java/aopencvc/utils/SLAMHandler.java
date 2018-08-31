package aopencvc.utils;

import com.example.aopencvc.androidopencvc.SDSLAM;

import org.opencv.core.Mat;

public class SLAMHandler {

    private SDSLAM activitySDSLAM;
    private long nativeSLAM = 0;

    public SLAMHandler() {
        this.nativeSLAM = CreateSLAM();
    }


    public String TrackFrame(Mat image) {
        return this.TrackFrame(this.nativeSLAM, 33, image.getNativeObjAddr());
    }

    // Native methods implemented by SLAM native library.


    private native long CreateSLAM();
    public native String TrackFrame(long slam, int param, long img);
}
