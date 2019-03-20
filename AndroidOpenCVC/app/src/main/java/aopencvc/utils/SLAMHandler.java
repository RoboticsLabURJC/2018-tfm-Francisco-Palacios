package aopencvc.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import org.opencv.core.Mat;

import java.io.File;


public class SLAMHandler {

    private long nativeSLAM = 0;

    public SLAMHandler() {
        this.nativeSLAM = createSLAM();
    }



    public String trackFrame(Mat image, Mat vKeyFramesPos, Mat planeEq, Mat worldPosPoint) {
        String a = this.trackFrame(this.nativeSLAM, 33, image.getNativeObjAddr(),
                vKeyFramesPos.getNativeObjAddr(), planeEq.getNativeObjAddr(),
                worldPosPoint.getNativeObjAddr());

        return a;
    }


    public void saveTraj(Context context){
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
        saveTrajectory(filepath, this.nativeSLAM);

        MediaScannerConnection.scanFile(context,new String[]{filepath},null,new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });
    }


    // Native methods implemented by SLAM native library.


    private native void saveTrajectory(String filepath, long slam);

    // Native methods implemented by SLAM native library.
    private native long createSLAM();

    public native String trackFrame(long slam, int param, long img, long vKFPs,
                                    long planeEq, long wPPoint);

}
