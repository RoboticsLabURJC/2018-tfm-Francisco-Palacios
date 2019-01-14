package aopencvc.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class CamTrail {


    private ArrayList<Float> trail;

    private final int mBytesPerFloat = 4;

    private int numPoints;

    private Shaders trailShad;

    public CamTrail(){
        trail = new ArrayList<Float>();
        numPoints = 0;
        trailShad = new Shaders();
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
        numPoints++;
    }

    public int getNumPoints(){
        return numPoints;
    }

    public FloatBuffer getFloatBufferTrail(){
        float[] trailArray = getTrail();

        FloatBuffer trailBuffer = ByteBuffer.allocateDirect(trailArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        trailBuffer.put(trailArray).position(0);
        return trailBuffer;
    }


    public int getProgramHandle(){
        return trailShad.getProgramHandle();
    }
	

}
