package aopencvc.opengl;


import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;


public class Arrow {
	
	
	    private ArrayList<Float> directions;

		private ArrayList<Float> positions;

		private int nArrows;


	
	public Arrow(){
		
        directions = new ArrayList<>();
		positions = new ArrayList<>();
	}

    public float[] getPoints(){
		float[] posArray;
		posArray = new float[positions.size()];
		for (int i = 0; i<positions.size();i++){
			posArray[i] = positions.get(i);
		}
		return posArray;
	}

	public float[] getDirections(){
		float[] dirArray;
		dirArray = new float[directions.size()];
		for (int i = 0; i<directions.size();i++){
			dirArray[i] = directions.get(i);
		}
		return dirArray;
	}
	
	
	public void addArrow(float [] newPoints, float[] newDirections){
        for (int i = 0;i<newPoints.length;i++){
            positions.add(newPoints[i]);
        }
		
		for (int i = 0;i<newDirections.length;i++){
            directions.add(newDirections[i]);
        }
        nArrows++;


    }

    public int getNArrows(){
		return nArrows;
	}

	public FloatBuffer[] getFloatBufferArrow(){


        float[] arrowHead = new float[]{
                0.0f,0.0f,1.0f,
                0.3f, 0.3f,0.0f,
                -0.3f, 0.3f, 0.0f,

				0.0f,0.0f,1.0f,
				0.3f, -0.3f,0.0f,
				-0.3f, -0.3f, 0.0f,

				0.0f,0.0f,1.0f,
				0.3f, 0.3f,0.0f,
				0.3f, -0.3f, 0.0f,

				0.0f,0.0f,1.0f,
				-0.3f, 0.3f,0.0f,
				-0.3f, -0.3f, 0.0f,

				0.3f, 0.3f,0.0f,
				-0.3f, 0.3f, 0.0f,
				-0.3f, -0.3f, 0.0f,

				0.3f, 0.3f,0.0f,
				0.3f, -0.3f, 0.0f,
				-0.3f, -0.3f, 0.0f

        };
		
		float[] arrowLine = new float[]{
			0.0f,0.0f,0.0f,
			0.0f,0.0f,-2.0f
		};
					
			
		
		FloatBuffer head = ByteBuffer.allocateDirect(arrowHead.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();


		head.put(arrowHead).position(0);
		
		FloatBuffer line = ByteBuffer.allocateDirect(arrowLine.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();


		line.put(arrowLine).position(0);
		
		
		return new FloatBuffer[]{head,line};
	}
	


}