package aopencvc.opengl;


import android.opengl.GLES20;
 


public class Arrow {
	
	
	    private ArrayList<Float> directions;
		private ArrayList<Float> positions;

	
	public Arrow(){
		
        directions = new ArrayList<Float>();
		positions = new ArrayList<Float>();

	}	
	
   public Object[] GetArrows(){
        float[] posArray;
        posArray = new float[positions.size()];
        for (int i = 0; i<positions.size();i++){
            posArray[i] = positions.get(i);
        }
		
		float[] dirArray;
        dirArray = new float[directions.size()];
        for (int i = 0; i<directions.size();i++){
            dirArray[i] = directions.get(i);
        }
        return new Object[] {posArray,dirArray};
    }
	
	
	public void AddArrow(float [] newPoints, float[] newDirections){
        for (int i = 0;i<newPoints.length;i++){
            positions.add(newPoints[i]);
        }
		
		for (int i = 0;i<newDirections.length;i++){
            directions.add(newDirections[i]);
        }

    }

	public FlotarBuffer[] getFloatBufferArrow(){
		
		
		float[] arrowHead = new float[]{
			0.0f,0.0f,1.0f,
			0.3f, 0.0f,0.0f,
			-0.3f, 0.0f, 0.0f
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