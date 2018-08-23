package aopencvc.opengl;

import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import aopencvc.opengl.ObjectInit;

import javax.microedition.khronos.opengles.GL10;


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

    private int xR = 0;

    private int yR = 0;

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

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;



    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                | GLES20.GL_DEPTH_BUFFER_BIT);

        long timeX = SystemClock.uptimeMillis() % 32000L;
        float xR = (360.0f / 32000.f) * ((int) timeX);

        long timeY = SystemClock.uptimeMillis() % 10000L;
        float yR = (360.0f / 10000.0f) * ((int) timeY);


        // Do a complete rotation every 10 seconds.

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, xR, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, yR, 0.0f, 1.0f, 0.0f);



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
        //GLES20.glShadeModel(GL10.GL_SMOOTH);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);


        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Position the eye behind the origin.
        final float eyeX = 2.0f;
        final float eyeY = 2.0f;
        final float eyeZ = 2.0f;

        // We are looking toward the distance
        final float lookX = -5.0f;
        final float lookY = -5.0f;
        //-------------Que diferencia hay entre poner -1.0 y -5.0-----------
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;



        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        int programHandle = init.getProgramHandle();
        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(programHandle, "v_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        GLES20.glViewport( 0, 0, width, height );
    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    public void drawObject(final FloatBuffer CoordinatesBuffer, final FloatBuffer GridBuffer,
                           final FloatBuffer CubeBuffer){


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glLineWidth(3.0f);



        CoordinatesBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        //GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        //CoordinatesBuffer.position(mColorOffset);


        //GLES20.glEnableVertexAttribArray(mColorHandle);

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





        //GridBuffer.position(0);
        //GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
         //       3*mBytesPerFloat, GridBuffer);

       // GLES20.glEnableVertexAttribArray(mPositionHandle);

        //GLES20.glUniform4fv(mColorHandle, 1, init.getGridColor(), 0);
        //GLES20.glDrawArrays(GLES20.GL_LINES, 0, 40);



    }

}