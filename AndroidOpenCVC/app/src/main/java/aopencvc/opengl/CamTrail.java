package aopencvc.opengl;

import android.os.SystemClock;

import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class CamTrail {

    private final int mBytesPerFloat = 4;

    private int numPoints;


    public CamTrail(){
        numPoints = 0;
    }

    /**
    Execute only after getFloatBufferTrail(Mat vKeyFramesPos)
     **/
    public int getNumPoints(){
        return numPoints;
    }

    public FloatBuffer getFloatBufferTrail(Mat vKeyFramesPos){
        if (vKeyFramesPos != null){
            float[] trailArray = new float[vKeyFramesPos.rows()*3];
            vKeyFramesPos.get(0,0,trailArray);
            for (int i = 0;i<trailArray.length;i++){
                trailArray[i] = (float) vKeyFramesPos.get(i/3,i%3)[0];
            }
            FloatBuffer trailBuffer = ByteBuffer.allocateDirect(trailArray.length * mBytesPerFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            trailBuffer.put(trailArray).position(0);
            numPoints = vKeyFramesPos.rows();
            return trailBuffer;
        }
        return null;
    }

}