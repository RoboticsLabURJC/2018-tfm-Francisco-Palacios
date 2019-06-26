package aopencvc.opengl;

import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;


import javax.microedition.khronos.opengles.GL10;

import aopencvc.utils.ExtrinsicsCalculator;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_64F;


public class ObjectRenderer implements GLSurfaceView.Renderer {

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];




    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;



	private CoordsObject coordsObject;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    private Shaders shaders;


    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /** How many elements per vertex. */
    private final int mStrideBytes = 3 * mBytesPerFloat;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;
    // tablet

/*
    private float cx = 644.2552668090187f;

    private float cy = 347.007496696664f;

    private float fx = 1080.33874061861f;

    private float fy = 1081.398961189691f;
*/
//kabel movil

    private float cx = 959.397404f;

    private float cy = 536.0155995f;

    private float fx = 1565.154521f;

    private float fy = 1565.505376f;


    private float[][] directions = {{1,0,0},{0,0,1},{1,0,0},{0,1,0}};
    private float[][] points = {{1.1287f,0.15325f,0.277f},{1.678f,0,0.872f},
                                {1.9765f,0,2.9335f},{4.3565f,0,2.9335f}};

    /*                         //1,0,0 // 0,1.3f,0
    //segundo intento
    private float[][] directions = {{1,0,0},{0,0,1},{1,0,0},{0,1,0}};
    private float[][] points = {{0,-1.3f,0},{1.2f,-1.3f,0},{1.2f,-1.3f,2.32f},{4.3f,-1.3f,2.32f}};
*/
/*
    private float[][] directions = {{1,0,0},{0,-1,0},{-1,0,0},{0,1,0}};
    private float[][] points = {{0,0.5f,-1f},{0.5f,0,-1f},{0,-0.5f,-1f},{-0.5f,0,-1f}};
*/

    private float[] objectPoint = {0.0f,0.0f,0.0f};//{-0.04275f,0.10815f,0.0f};

    private Arrow arrows;
    private Mat vKeyFramesPos;
    private Mat planeEquation;
    private Mat cameraPose;
    private Float prev;
    private CamTrail camTrail;


    private boolean usingArrows = true;


    public ObjectRenderer(){
        planeEquation = new Mat(1,4, CV_32F, Scalar.all(0.0));
        cameraPose = new Mat(4,4, CV_64F, Scalar.all(0.0));
    }

    public void putVKeyFramesPos(Mat vKFPs){
        vKeyFramesPos = vKFPs;
    }

    public void putPlaneEquation(Mat pe){
        planeEquation = pe;
    }

    public void putCameraPose(Mat cp){
        cameraPose = cp;
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                | GLES20.GL_DEPTH_BUFFER_BIT);


        Matrix.setIdentityM(mModelMatrix, 0);

        float[] mPlaneParams = new float[4];
        for (int i = 0;mPlaneParams.length>i;i++){
            mPlaneParams[i] = (float) planeEquation.get(0,i)[0];
        }
        planeEquation.get(0,0,mPlaneParams);


        float[] point = {0.0f,0.0f,2f};

        //long a = System.currentTimeMillis() % 2000;
        //objectPoint[2] = -2+( a / 2000.0f);


        //Debemos rotar la normal 180º alrededor de la X como hicimos con la pose. Esto es por que
        //aun que anteriormente no rotamos lo puntos, ahora la normal esta mal calculada y esta apuntando e
        // Z positivo, cuando lo que queremos es que apunte en Z negativo.

        //Una rotacion de ese estilo pondria los parametros de Y y Z en direccion contraria, ya que
        //estamos dando media vuelta alrededor de la X.
		/// Hemos rotado los puntos en el codigo C. Vamos a probar asi a ver que tal.


        //-------------------------------Modelo translacion rotacion--------------------------------

        float[] normal = {mPlaneParams[0],mPlaneParams[1],mPlaneParams[2]};


//        Matrix.translateM(mModelMatrix,0, point[0], point[1], point[2]);
        //float[] modelRotation = parallelizeVectors(new float[] {0.0f,1.0f,0.0f}, normal);
      //  Matrix.rotateM(mModelMatrix, 0, modelRotation[3]*57.2958f, modelRotation[0],
       //         modelRotation[1],modelRotation[2]);

        //Matrix.scaleM(mModelMatrix,0,0.25f,0.25f,0.25f);
        coordsObject.putPoint(objectPoint);
        //coordsObject.putModelRotation(modelRotation);
        //------------------------------------------------------------------------------------------



        mViewMatrix = ExtrinsicsCalculator.getSolvePnpPose();
        if(mViewMatrix == null){
            mViewMatrix = new float[16];
            Matrix.setIdentityM(mViewMatrix, 0);
        }else{
            float[] poseCV = new float [16];

            for (int i = 0;i<poseCV.length;i++) {
                poseCV[i] = (float) cameraPose.get(i % cameraPose.cols(),
                        i / cameraPose.cols())[0];
            }

            Matrix.multiplyMM(mViewMatrix,0,mViewMatrix,0,poseCV,0);
            Matrix.rotateM(mViewMatrix,0,180,1,0,0);
        }

        //Matrix.setIdentityM(mViewMatrix, 0);



        //Matrix.translateM(mViewMatrix,0,0,0,0);









/*
        for (int i = 0;i<mViewMatrix.length;i++) {
            mViewMatrix[i] = (float) cameraPose.get(i % cameraPose.cols(),
                    i / cameraPose.cols())[0];
        }
        */
        //System.out.println(mViewMatrix[12] + " " + mViewMatrix[13] + " " + mViewMatrix[14]);



        //mViewMatrix = ObjectRenderer.ChangeXDirection(mViewMatrix);

      //  Matrix.setIdentityM(mViewMatrix, 0);

        //Matrix.rotateM(mViewMatrix,0,315,0,1,0);
        //Matrix.translateM(mViewMatrix,0,-1,-1,-1);



        //Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, matrixTranslation, 0);


/*
        if (usingArrows){
            float[] tOffSet = ExtrinsicsCalculator.getCameraTranslation();
            if (tOffSet != null){
                float[] offset = new float[16];
                float[] rotation = new float[16];
                System.out.println(Arrays.toString(tOffSet));
                Matrix.translateM(mViewMatrix,0,tOffSet[0],tOffSet[1],tOffSet[2]);


                Matrix.setIdentityM(offset, 0);
                Matrix.setIdentityM(rotation, 0);
                offset[12] = tOffSet[0];
                offset[13] = tOffSet[1];
                offset[14] = tOffSet[2];

                rotation[0] = mViewMatrix[0];
                rotation[1] = mViewMatrix[1];
                rotation[2] = mViewMatrix[2];
                rotation[3] = mViewMatrix[4];
                rotation[4] = mViewMatrix[5];
                rotation[5] = mViewMatrix[6];
                rotation[6] = mViewMatrix[8];
                rotation[7] = mViewMatrix[9];
                rotation[8] = mViewMatrix[10];

                Matrix.multiplyMM(tOffSet, 0, rotation, 0, offset, 0);


                mViewMatrix[12] = mViewMatrix[12] + tOffSet[0];
                mViewMatrix[13] = mViewMatrix[13] + tOffSet[1];
                mViewMatrix[14] = mViewMatrix[14] + tOffSet[2];
                System.out.println("Offseeett: " +tOffSet[2]);

            }


        }
*/



        //mViewMatrix[14] = a/2000.f;

        draw();
    }


    public void useProgram(int programHandle){

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(programHandle, "v_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }


    public float[] vectorialProduct(float[] v1, float[] v2){
        float[] vProdV1V2 = {v1[1]*v2[2]-v1[2]*v2[1],-(v1[0]*v2[2]-v1[2]*v2[0]),v1[0]*v2[1]-v1[1]*v2[0]};
        return vProdV1V2;
    }


    //v1 es el vector de (0,0,1)
    //Este metodo paraleliza el vector 1 con el vector 2.
    public float[] parallelizeVectors(float[] v1, float[] v2){
        float modV1 = (float) Math.sqrt(Math.pow(v1[0],2)+Math.pow(v1[1],2)+Math.pow(v1[2],2));
        float modV2 = (float) Math.sqrt(Math.pow(v2[0],2)+Math.pow(v2[1],2)+Math.pow(v2[2],2));


        float cosAngle = (v1[0]*v2[0]+v1[1]*v2[1]+v1[2]*v2[2])/(modV1*modV2);
        float angle = (float) Math.acos(cosAngle);

        float[] vProdV1V2 = vectorialProduct(v1,v2);

        return new float[] {vProdV1V2[0],vProdV1V2[1],vProdV1V2[2],angle};
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {


        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        GLES20.glClearColor(0,0,0,0);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        GLES20.glCullFace(GLES20.GL_FRONT_AND_BACK);



        coordsObject = new CoordsObject();
        camTrail = new CamTrail();
        arrows = new Arrow();
        for (int i = 0;i<directions.length;i++){
            arrows.AddArrow(points[i],directions[i]);
        }


        //Coordenadas y rotacion para el objeto
        Matrix.setIdentityM(mModelMatrix, 0);

        //Debemos rotar la normal 180º alrededor de la X como hicimos con la pose. Esto es por que
        //aun que anteriormente no rotamos lo puntos, ahora la normal esta mal calculada y esta apuntando e
        // Z positivo, cuando lo que queremos es que apunte en Z negativo.

        //Una rotacion de ese estilo pondria los parametros de Y y Z en direccion contraria, ya que
        //estamos dando media vuelta alrededor de la X.
        /// Hemos rotado los puntos en el codigo C. Vamos a probar asi a ver que tal.


        //-------------------------------Modelo translacion rotacion--------------------------------


        shaders = new Shaders();
        useProgram(shaders.getProgramHandle());


        // Position the eye behind the origin.
        final float eyeX = 1.0f;
        final float eyeY = 1.0f;
        final float eyeZ = 1.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 1.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        //Añadiendo las flechas que se renderizaran despues


        //Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);




        //Matrix.setIdentityM(mViewMatrix, 0);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {

        GLES20.glViewport(0, 0, width, height);


        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;

        float zfar = 100.0f;
        float znear = 0.05f;

        float[] persp = new float[16];
        float[] ndc = new float[16];


        persp[0] = fx;
        persp[5] = fy;
        persp[8] = cx;
        persp[9] = cy;
        persp[10] = znear+zfar;
        persp[11] = 1;
        persp[15] = znear*zfar;

        Matrix.orthoM(ndc,0,0,width,height,0,znear,zfar);


        Matrix.multiplyMM(mProjectionMatrix,0,ndc,0,persp,0);
        System.out.println(Arrays.toString(mProjectionMatrix));


        mProjectionMatrix[0] = 2*fx/width;
        mProjectionMatrix[1] = 0.0f;
        mProjectionMatrix[2] = 0.0f;
        mProjectionMatrix[3] = 0.0f;

        mProjectionMatrix[4] = 0.0f;
        mProjectionMatrix[5] = 2 * fy / height;
        mProjectionMatrix[6] = 0.0f;
        mProjectionMatrix[7] = 0.0f;

        mProjectionMatrix[8] = 1.0f - (2*cx/width);
        mProjectionMatrix[9] = (2*cy/height) - 1.0f;
        mProjectionMatrix[10] = (zfar+znear)/(znear-zfar);
        mProjectionMatrix[11] = -1.0f;

        mProjectionMatrix[12] = 0.0f;
        mProjectionMatrix[13] = 0.0f;
        mProjectionMatrix[14] = (2.0f * zfar * znear) / (znear-zfar);
        mProjectionMatrix[15] = 0.0f;

        System.out.println(Arrays.toString(mProjectionMatrix));

/*
        Matrix.setIdentityM(mProjectionMatrix,0);
       Matrix.frustumM(
                mProjectionMatrix, 0,
                left, right, bottom, top,
                znear, zfar);
*/

    }


	
	public void drawObject(final FloatBuffer CoordsBuffer, float[] color, int paintFunction, int nPoints, int offset){
		
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, CoordsBuffer);
		
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glDrawArrays(paintFunction, offset, nPoints);

	}
	
	public void transformModel(float[] rotationVecParallel, float[] translation, float[] modelRotation){

		Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.setIdentityM(mMVPMatrix, 0);
        if (rotationVecParallel != null || translation != null || modelRotation != null) {
            Matrix.translateM(mModelMatrix, 0, translation[0], translation[1], translation[2]);

            if (modelRotation == null) {
                float[] vecToParallel = new float[]{0.0f, 0.0f, 1.0f};
                if (Arrays.equals(rotationVecParallel,vecToParallel)){
                    modelRotation = null;
                }else if (Arrays.equals(rotationVecParallel,new float[]{0.0f, 0.0f, -1.0f})) {
                    modelRotation = new float[]{0.0f, 1.0f, 0.0f,(float)Math.PI};
                }else{
                    if (rotationVecParallel != null){
                        modelRotation = parallelizeVectors(vecToParallel, rotationVecParallel);
                    }
                }

            }
            if (modelRotation != null){
                Matrix.rotateM(mModelMatrix, 0, modelRotation[3] * 57.2958f, modelRotation[0],
                        modelRotation[1], modelRotation[2]);
            }

             Matrix.scaleM(mModelMatrix, 0, 0.057f, 0.057f, 0.057f);


        }


//		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);


  //      Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        float[] viewInverted = new float[16];
        Matrix.invertM(viewInverted,0,mViewMatrix,0);
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, viewInverted, 0);
        //Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, viewInverted, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glLineWidth(3.0f);
        //GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0); //Necesario para camTrail


	}



    public void draw(){

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        /*
        // Paint arrows
		int nArrows = arrows.getNArrows();
        float[] directionsArr = arrows.getDirections();
        float[] pointsArr = arrows.getPoints();
		FloatBuffer[] arrowBuffer = arrows.getFloatBufferArrow();
		for (int i = 0; i<nArrows*3;i=i+3){
			transformModel(new float[]{directionsArr[i],directionsArr[i+1],directionsArr[i+2]},
                    new float[]{pointsArr[i],pointsArr[i+1],pointsArr[i+2]}, null);

            drawObject(arrowBuffer[0], new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_TRIANGLES,3*6,0);
            drawObject(arrowBuffer[1], new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_LINES,2,0);

		}
*/
/*
        transformModel(new float[]{1,0,0},
                new float[]{0,1.3f,1}, null);



        FloatBuffer[] arrowBuffer = arrows.getFloatBufferArrow();

        drawObject(arrowBuffer[0], new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_TRIANGLES,3,0);
        drawObject(arrowBuffer[1], new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_LINES,2,0);
*/




        transformModel(null,coordsObject.getPoint(),null); //coordsObject.getModelRotation());

        // Draw coords object
        //transformModel(null,coordsObject.getPoint(),coordsObject.getModelRotation());


       // GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mPositionHandle);

		//FloatBuffer GridBuffer = coordsObject.getGrid();
		//drawObject(GridBuffer, new float[]{1.0f,1.0f,1.0f,1.0f},GLES20.GL_LINES,40, 0);
		//FloatBuffer CubeBuffer = coordsObject.getCube();
		//drawObject(CubeBuffer, new float[]{1.0f,1.0f,1.0f,1.0f},GLES20.GL_POINTS,8, 0);

		FloatBuffer CoordinatesBuffer = coordsObject.getObjectcoordinates();
		drawObject(CoordinatesBuffer, new float[]{1.0f,0.0f,0.0f,1.0f},GLES20.GL_LINES,2, 0);
		drawObject(CoordinatesBuffer, new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_LINES,2, 2);
		drawObject(CoordinatesBuffer, new float[]{0.0f,0.0f,1.0f,1.0f},GLES20.GL_LINES,2, 4);



/*
        transformModel(null, null, null);
        FloatBuffer trailBuffer = camTrail.getFloatBufferTrail(vKeyFramesPos);
        if (trailBuffer != null) {
            drawObject(trailBuffer, new float[]{0.0f,1.0f,0.0f,1.0f},GLES20.GL_LINE_STRIP,camTrail.getNumPoints(),0);
        }
*/
/*
        GLES20.glDisable(mColorHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
*/
    }


}