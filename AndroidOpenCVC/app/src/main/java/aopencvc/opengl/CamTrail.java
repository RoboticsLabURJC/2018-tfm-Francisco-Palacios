package aopencvc.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class CamTrail {


    private ArrayList<Float> trail;

    private final int mBytesPerFloat = 4;



    public CamTrail(){
        trail = new ArrayList<Float>();
    }


    public float[] getTrail(){
        float[] trailArray;
        trailArray = new float[trail.size()];
        for (int i = 0; i<trail.size();i++){
            trailArray[i] = trail.get(i);
        }

        return trailArray;
    }


    public void AddTrailData(float [] newPoints){
        for (int i = 0;i<newPoints.length;i++){
            trail.add(newPoints[i]);
        }

    }

    public FloatBuffer getFloatBufferTrail(){
        float[] trailArray = getTrail();

        FloatBuffer trailBuffer = ByteBuffer.allocateDirect(trailArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        trailBuffer.put(trailArray).position(0);
        return trailBuffer;
    }

	

}
