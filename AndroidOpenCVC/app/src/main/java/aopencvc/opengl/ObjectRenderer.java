package aopencvc.opengl;

import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.nio.FloatBuffer;
import java.util.Arrays;


import javax.microedition.khronos.opengles.GL10;

import static org.opencv.core.CvType.CV_64F;


public class ObjectRenderer implements GLSurfaceView.Renderer {

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] malo = new float[16];
    private float[] mModelMatrix = new float[16];

    private float[] mCurrentRotationTranslation = new float[16];



    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    private float width = 1280.0f;

    private float height = 720.0f;

    private ObjectInit init;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;



    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /** How many elements per vertex. */
    private final int mStrideBytes = 3 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;


    private float cx = 1080.3387f;

    private float cy = 1081.3989f;

    private float fx = 644.2552f;

    private float fy = 347.0074f;

    private Mat cameraRotation;
    private Mat cameraTranslation;
    private Mat worldPosPoint;




    public ObjectRenderer(){
        cameraRotation = new Mat(1,3, CV_64F, Scalar.all(0.0));
        cameraTranslation = new Mat(1,3, CV_64F, Scalar.all(0.0));
        worldPosPoint = new Mat(1,3, CV_64F, Scalar.all(5.0));
    }

    public void putCameraRotation(Mat cr){
        cameraRotation = cr;
    }

    public void putCameraTranslation(Mat ct){
        cameraTranslation = ct;
    }

    public void putWorldPosPoint(Mat wpp){
        worldPosPoint = wpp;
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                | GLES20.GL_DEPTH_BUFFER_BIT);


        Matrix.setIdentityM(mModelMatrix, 0);


        Matrix.translateM(mModelMatrix,0, -(float) worldPosPoint.get(0,0)[0],
                -(float) worldPosPoint.get(0,1)[0],
                -(float) worldPosPoint.get(0,2)[0]);



        Matrix.setIdentityM(mCurrentRotationTranslation, 0);


        Matrix.translateM(mCurrentRotationTranslation, 0, (float) -cameraTranslation.get(0,0)[0],
                (float) cameraTranslation.get(0,1)[0],
                (float) cameraTranslation.get(0,2)[0]);

        Matrix.rotateM(mCurrentRotationTranslation, 0, (float) cameraRotation.get(0,0)[0], -1.0f,
                0.0f, 0.0f);
        Matrix.rotateM(mCurrentRotationTranslation, 0, (float) cameraRotation.get(0,1)[0], 0.0f,
                1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotationTranslation, 0, (float) cameraRotation.get(0,2)[0], 0.0f,
                0.0f, 1.0f);






        drawObject(init.getObjectcoordinates(),init.getGrid(),init.getCube());


    }



    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        init = new ObjectInit();

        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        GLES20.glClearColor(0,0,0,0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);




        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.0f;

        // We are looking toward the distance
        final float lookX = -1.0f;
        final float lookY = -1.0f;
        final float lookZ = -1.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;



        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        int programHandle = init.getProgramHandle();
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(programHandle, "v_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        //GLES20.glViewport(0, 0, width, height);


        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 2.0f;
        final float far = 50.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        /*
        float zfar = 10000.0f;
        float znear = 0.1f;
        mProjectionMatrix[0] = 2.0f*fx/this.width;
        mProjectionMatrix[1] = 0.0f;
        mProjectionMatrix[2] = (this.width-2.0f*cx)/this.width;
        mProjectionMatrix[3] = 0.0f;

        mProjectionMatrix[4] = 0.0f;
        mProjectionMatrix[5] = 2.0f*fy/this.height;
        mProjectionMatrix[6] = (-this.height+2.0f*cy)/this.height;
        mProjectionMatrix[7] = 0.0f;

        mProjectionMatrix[8] = 0.0f;
        mProjectionMatrix[9] = 0.0f;
        mProjectionMatrix[10] = (-zfar-znear)/(zfar-znear);
        mProjectionMatrix[11] = (-2.0f * zfar * znear) / (zfar-znear);

        mProjectionMatrix[12] = 0.0f;
        mProjectionMatrix[13] = 0.0f;
        mProjectionMatrix[14] = -1.0f;
        mProjectionMatrix[15] = 0.0f;
        */

        System.out.println("malo: " + Arrays.toString(malo));
        System.out.println("bueno: " + Arrays.toString(mProjectionMatrix));




    }



    public void drawObject(final FloatBuffer CoordinatesBuffer, final FloatBuffer GridBuffer,
                           final FloatBuffer CubeBuffer){


        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mCurrentRotationTranslation, 0, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glLineWidth(3.0f);



        CoordinatesBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, GridBuffer);
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 40);

        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, CubeBuffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 8);

        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, CoordinatesBuffer);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{1.0f,0.0f,0.0f,1.0f}, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{0.0f,1.0f,0.0f,1.0f}, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 2, 2);
        GLES20.glUniform4fv(mColorHandle, 1, new float[]{0.0f,0.0f,1.0f,1.0f}, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 4, 2);



        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisable(mColorHandle);






    }

}