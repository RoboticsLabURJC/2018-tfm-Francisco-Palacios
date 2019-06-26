package aopencvc.utils;
import org.opencv.calib3d.Calib3d; //Solve pnp
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_64F;

import java.util.ArrayList;
import java.util.List;


public class ExtrinsicsCalculator {


    MatOfPoint3f extCorner3D;
    List<Point>  listExtCorners;
    Mat cameraMatrix; //Intrinsics
    MatOfDouble distCoeffsMat;

     public ExtrinsicsCalculator(){
         List<Point3> objectPoints = new ArrayList<>();
         objectPoints.add(new Point3(0,0,0)); ///leftup corner 3d
         objectPoints.add(new Point3(0,0,0)); //rightup corner 3d
         objectPoints.add(new Point3(0,0,0));  //leftdown corner 3d
         objectPoints.add(new Point3(0,0,0));  //rightdown corner 3d

         extCorner3D = new MatOfPoint3f();
         extCorner3D.fromList(objectPoints);

         listExtCorners = new ArrayList();
         cameraMatrix = new Mat(3,3,CV_64F,Scalar.all(0.0));
         cameraMatrix.put(0,0,1080.33874061861d);
         cameraMatrix.put(0,2,644.2552668090187d);
         cameraMatrix.put(1,1,1081.398961189691d);
         cameraMatrix.put(1,2,347.007496696664d);
         cameraMatrix.put(0,0,1d);


         List<Double> distCoeffs = new ArrayList<>();
         distCoeffs.add(0.1390849622709781d);
         distCoeffs.add(-0.9989370047449903d);
         distCoeffs.add(0.0009328385593714286d);
         distCoeffs.add(0.00003818329618718235d);
         distCoeffs.add(2.231184493469707d);

         MatOfDouble distCoeffsMat = new MatOfDouble();
         distCoeffsMat.fromList(distCoeffs);
     }


//[1080.33874061861, 0, 644.2552668090187;
//  0, 1081.398961189691, 347.007496696664;
//  0, 0, 1]
//Distortion Vector:
//[0.1390849622709781;
//  -0.9989370047449903;
//  0.0009328385593714286;
//  0.00003818329618718235;
//  2.231184493469707]
//Average error was: 0.1867


     private double[][] SolvePnP(Mat image){
         MatOfPoint2f corners = new MatOfPoint2f();
         Calib3d.findChessboardCorners(image,new Size(4,6),corners);

         List<Point> listOfPoints = corners.toList();
         listExtCorners.add(listOfPoints.get(0));
         listExtCorners.add(listOfPoints.get(3));
         listExtCorners.add(listOfPoints.get(20));
         listExtCorners.add(listOfPoints.get(23));
         MatOfPoint2f extCorners = new MatOfPoint2f();
         extCorners.fromList(listExtCorners);


         Mat rvec = new Mat(3,1,CV_64F);
         Mat tvec = new Mat(3,1,CV_64F);
         Calib3d.solvePnP(extCorner3D,extCorners,cameraMatrix,distCoeffsMat,rvec,tvec,false,Calib3d.CV_P3P);

         Mat rotation = new Mat(3,3,CV_64F);

         Calib3d.Rodrigues(rvec,rotation);

         double[][] result = new double[4][4]; //rows cols

         result[0][0] = rotation.get(0,0)[0];
         result[0][1] = rotation.get(0,1)[0];
         result[0][2] = rotation.get(0,2)[0];
         result[0][3] = tvec.get(0,0)[0];

         result[1][0] = rotation.get(1,0)[0];
         result[1][1] = rotation.get(1,1)[0];
         result[1][2] = rotation.get(1,2)[0];
         result[1][3] = tvec.get(1,0)[0];

         result[2][0] = rotation.get(2,0)[0];
         result[2][1] = rotation.get(2,1)[0];
         result[2][2] = rotation.get(2,2)[0];
         result[2][3] = tvec.get(2,0)[0];

         result[3][0] = 0;
         result[3][1] = 0;
         result[3][2] = 0;
         result[3][3] = 1;


         return result;
     }


}
