package aopencvc.opengl;

import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class CamTrail {

    private final int mBytesPerFloat = 4;

    private int numPoints;


    public CamTrail(){
        numPoints = 0;
    }



    public int getNumPoints(){
        return numPoints;
    }

    public FloatBuffer getFloatBufferTrail(Mat vKeyFramesPos){
        float[] trailArray = new float[vKeyFramesPos.rows()*3];
        vKeyFramesPos.get(0,0,trailArray);


        FloatBuffer trailBuffer = ByteBuffer.allocateDirect(trailArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        trailBuffer.put(trailArray).position(0);
        numPoints = vKeyFramesPos.rows();
        return trailBuffer;
    }

}