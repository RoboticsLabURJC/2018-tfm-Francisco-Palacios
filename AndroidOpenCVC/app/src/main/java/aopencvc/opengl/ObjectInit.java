package aopencvc.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class ObjectInit {
    // New class members
    /**
     * Store our model data in a float buffer.
     */
    private final FloatBuffer coordinates;

    private final FloatBuffer grid;

    private final FloatBuffer cube;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    /**
     * Initialize the model data.
     */
    final String vertexShader =
            "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.

            + "attribute vec4 a_Position;     \n" // Per-vertex position information we will pass in.
            //+ "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.

           // + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.

            + "void main()                    \n"     // The entry point for our vertex shader.
            + "{                              \n"
           // + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
            // It will be interpolated across the triangle.
            + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
            + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
            + "   gl_PointSize = 8.0; \n"
            + "}                              \n";    // normalized screen coordinates.


    final String fragmentShader =
            "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
            // precision in the fragment shader.
            + "uniform vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
            // triangle per fragment.
            + "void main()                    \n"     // The entry point for our fragment shader.
            + "{                              \n"
            + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
            + "}                              \n";

    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private int programHandle;
    private float[] gridColor;

    public ObjectInit() {
        // Drawing lines
        final float[] coordinatesData = {
                // X, Y, Z,
                // R, G, B, A
                0.0f, 0.0f, 0.0f,

                1.5f, 0.0f, 0.0f,

                0.0f, 0.0f, 0.0f,

                0.0f, 1.5f, 0.0f,

                0.0f, 0.0f, 0.0f,

                0.0f, 0.0f, 1.5f};

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


        loadVShaders();
        loadFShaders();
        bindShaders();


    }


    public float[] getGridColor() {
        return gridColor;
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


    private void loadVShaders(){
        vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

    }


    private void loadFShaders(){
        fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle  != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle , fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle , GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle );
                fragmentShaderHandle  = 0;
            }
        }

        if (fragmentShaderHandle  == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

    }



    private void bindShaders(){

        programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }
    }

    public int getProgramHandle(){
        return programHandle;
    }

}