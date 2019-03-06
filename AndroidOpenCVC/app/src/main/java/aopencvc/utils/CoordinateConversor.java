package aopencvc.utils;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Scalar;

import static org.opencv.core.CvType.CV_64F;

public class CoordinateConversor {

    static private Mat rotRealToCV;  //Rotation mat from real world to opencv camera solvePnP
    static private Mat trRealToCV;  //translation mat from real world to opencv camera  solvePnP

    static private Mat rotCCVToCGL;  //Rotation mat from opencv camera to gles camera. No need of translation conversion
                                    // since the both of them must be in the same point. fija, mirar en casa cuaderno para saber
                                    //CGL y poder inferir la rotacion

    static private Mat rotCGLToOGL;  //Rotation mat from opengl camera to opengl object/model, fija, mirar cuaderno

    static private Mat trCGLToOGL;  //translation mat from opengl camera to opengl object/model, fija, mirar cuaderno


    public static void UpdateCGLToOGL(){

    }

    private static Mat MultiplicateMatrixes(Mat a, Mat b){
        Mat c = new Mat(a.rows(),b.cols(),CV_64F, Scalar.all(0.0d));
        Core.multiply(a,b,c);
        return c;
    }

    public static Mat GetCVfromReal(double[] point){
        Mat mPoint = arrayPointToMatPoint(point);

        Mat mPointRotated = MultiplicateMatrixes(rotRealToCV, mPoint);

        Mat transformed = new Mat(3,1,CV_64F);
        Core.add(mPointRotated,trRealToCV,transformed);

        return transformed;

    }

    public static Mat GetCGLfromCCV(double[] point){
        Mat mPoint = arrayPointToMatPoint(point);

        Mat mPointRotated = MultiplicateMatrixes(rotCCVToCGL, mPoint);


        return mPointRotated;

    }

    public static Mat GetOGLfromCGL(double[] point){
        Mat mPoint = arrayPointToMatPoint(point);

        Mat mPointRotated = MultiplicateMatrixes(rotCGLToOGL, mPoint);

        Mat transformed = new Mat(3,1,CV_64F);
        Core.add(mPointRotated,trCGLToOGL,transformed);

        return transformed;

    }


    private static Mat arrayPointToMatPoint(double[] point){
        Mat mPoint = new Mat(3,1,CV_64F);
        mPoint.put(0,0,point[0]);
        mPoint.put(1,0,point[1]);
        mPoint.put(2,0,point[2]);
        return mPoint;
    }

}
