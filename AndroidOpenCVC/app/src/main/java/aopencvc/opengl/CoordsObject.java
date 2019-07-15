package aopencvc.opengl;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;



public class  CoordsObject {


    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer coordinates;

    private final FloatBuffer grid;

    private final FloatBuffer cube;

    private float[] point;

    private float[] modelRotation;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

	public static float cellSideSice = 0.0285f;

	public CoordsObject() {

		// Drawing lines
		final float[] coordinatesData = {
				// X, Y, Z
				0.0f, 0.0f, 0.0f,

				cellSideSice*5, 0.0f, 0.0f,

				0.0f, 0.0f, 0.0f,

				0.0f, cellSideSice*5, 0.0f,

				0.0f, 0.0f, 0.0f,

				0.0f, 0.0f, cellSideSice*5};
/*
		final float[] gridData = {
				0.0f, 0.0f, 0.0f,
				2.16f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.24f,
				2.16f, 0.0f, 0.24f,
				0.0f, 0.0f, 0.48f,
				2.16f, 0.0f, 0.48f,
				0.0f, 0.0f, 0.72f,
				2.16f, 0.0f, 0.72f,
				0.0f, 0.0f, 0.96f,
				2.16f, 0.0f, 0.96f,
				0.0f, 0.0f, 1.20f,
				2.16f, 0.0f, 1.20f,
				0.0f, 0.0f, 1.44f,
				2.16f, 0.0f, 1.44f,
				0.0f, 0.0f, 1.68f,
				2.16f, 0.0f, 1.68f,
				0.0f, 0.0f, 1.92f,
				2.16f, 0.0f, 1.92f,
				0.0f, 0.0f, 2.16f,
				2.16f, 0.0f, 2.16f,


				0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 2.16f,
				0.24f, 0.0f, 0.0f,
				0.24f, 0.0f, 2.16f,
				0.48f, 0.0f, 0.0f,
				0.48f, 0.0f, 2.16f,
				0.72f, 0.0f, 0.0f,
				0.72f, 0.0f, 2.16f,
				0.96f, 0.0f, 0.0f,
				0.96f, 0.0f, 2.16f,
				1.20f, 0.0f, 0.0f,
				1.20f, 0.0f, 2.16f,
				1.44f, 0.0f, 0.0f,
				1.44f, 0.0f, 2.16f,
				1.68f, 0.0f, 0.0f,
				1.68f, 0.0f, 2.16f,
				1.92f, 0.0f, 0.0f,
				1.92f, 0.0f, 2.16f,
				2.16f, 0.0f, 0.0f,
				2.16f, 0.0f, 2.16f
		};
*/
		final float[] gridData = {
				0.0f, 0.0f, 0.0f,
				0.0f, cellSideSice*8, 0.0f,
				cellSideSice, 0.0f, 0.0f,
				cellSideSice, cellSideSice*8, 0.0f,
				cellSideSice*2, 0.0f, 0.0f,
				cellSideSice*2, cellSideSice*8, 0.0f,
				cellSideSice*3, 0.0f, 0.0f,
				cellSideSice*3, cellSideSice*8, 0.0f,
				cellSideSice*4, 0.0f, 0.0f,
				cellSideSice*4, cellSideSice*8, 0.0f,
				cellSideSice*5, 0.0f, 0.0f,
				cellSideSice*5, cellSideSice*8, 0.0f,
				0.0f, 0.0f, 0.0f,
				cellSideSice*5, 0.0f, 0.0f,
				0.0f, cellSideSice, 0.0f,
				cellSideSice*5, cellSideSice, 0.0f,
				0.0f, cellSideSice*2, 0.0f,
				cellSideSice*5, cellSideSice*2, 0.0f,
				0.0f, cellSideSice*3, 0.0f,
				cellSideSice*5, cellSideSice*3, 0.0f,
				0.0f, cellSideSice*4, 0.0f,
				cellSideSice*5, cellSideSice*4, 0.0f,
				0.0f, cellSideSice*5, 0.0f,
				cellSideSice*5, cellSideSice*5, 0.0f,
				0.0f, cellSideSice*6, 0.0f,
				cellSideSice*5, cellSideSice*6, 0.0f,
				0.0f, cellSideSice*7, 0.0f,
				cellSideSice*5, cellSideSice*7, 0.0f

		};

		final float[] cubeData = {
				0.75f,0.75f, 0.0f,
				1.35f, 0.75f, 0.0f,
				0.75f,1.35f, 0.0f,
				1.35f, 1.35f, 0.0f,

				0.75f,0.75f, -0.6f,
				1.35f, 0.75f, -0.6f,
				0.75f,1.35f, -0.6f,
				1.35f, 1.35f, -0.6f
		};



		// Initialize the buffers.
		coordinates = ByteBuffer.allocateDirect(coordinatesData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();


		coordinates.put(coordinatesData).position(0);

		grid = ByteBuffer.allocateDirect(gridData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();


		grid.put(gridData).position(0);


		cube = ByteBuffer.allocateDirect(cubeData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();


		cube.put(cubeData).position(0);


	}
	
	public void putPoint(float [] _point){
		point = _point;
	}

	public float[] getPoint(){
		return point;
	}

	public void putModelRotation(float[] _modelRotation){
		modelRotation = _modelRotation;
	}

	public float[] getModelRotation(){
		return modelRotation;
	}

    public FloatBuffer getObjectcoordinates(){
        return coordinates;
    }

    public FloatBuffer getGrid() {
        return grid;
    }

    public FloatBuffer getCube() {
        return cube;
    }

}