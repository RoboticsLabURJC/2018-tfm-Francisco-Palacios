package aopencvc.utils;
import android.opengl.Matrix;

import org.opencv.calib3d.Calib3d; //Solve pnp
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_64F;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExtrinsicsCalculator {


     private static MatOfPoint3f extCorner3D;
     private static List<Point>  listExtCorners;
     private static Mat cameraMatrix; //Intrinsics
     private static MatOfDouble distCoeffsMat;

     private static double[][] solvePnpResult;

     private static float[] pnpPose;

     public ExtrinsicsCalculator(){


     }

     private static void init(){
         pnpPose = new float[16];
         solvePnpResult = new double[4][4];
         List<Point3> objectPoints = new ArrayList<>();
         /*
         objectPoints.add(new Point3(1.086f,-0.2245f,-0.277f)); ///leftup corner 3d
         objectPoints.add(new Point3(1.1715f,-0.2245f,-0.277f)); //rightup corner 3d
         objectPoints.add(new Point3(1.086f,-0.082f,-0.277f));  //leftdown corner 3d
         objectPoints.add(new Point3(1.1715f,-0.082f,-0.277f));  //rightdown corner 3d
        */
         objectPoints.add(new Point3(0.0f,0.0f,0.0f)); ///leftup corner 3d
         objectPoints.add(new Point3(0.0855f,0.0f,0.0f)); //rightup corner 3d
         objectPoints.add(new Point3(0.0f,0.1425f,0.0f));  //leftdown corner 3d
         objectPoints.add(new Point3(0.0855f,0.1425f,0.0f));  //rightdown corner 3d
         extCorner3D = new MatOfPoint3f();
         extCorner3D.fromList(objectPoints);

         listExtCorners = new ArrayList();
         cameraMatrix = new Mat(3,3,CV_32F,Scalar.all(0.0));
         cameraMatrix.put(0,0,1080.33874061861d);
         cameraMatrix.put(0,2,644.2552668090187d);
         cameraMatrix.put(1,1,1081.398961189691d);
         cameraMatrix.put(1,2,347.007496696664d);
         cameraMatrix.put(2,2,1d);


         List<Double> distCoeffs = new ArrayList<>();
         distCoeffs.add(0.1390849622709781d);
         distCoeffs.add(-0.9989370047449903d);
         distCoeffs.add(0.0009328385593714286d);
         distCoeffs.add(0.00003818329618718235d);
         distCoeffs.add(2.231184493469707d);

         distCoeffsMat = new MatOfDouble();
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


     public static void SolvePnP(Mat image){
         init();
         MatOfPoint2f corners = new MatOfPoint2f();
         Calib3d.findChessboardCorners(image,new Size(4,6),corners);

         List<Point> listOfPoints = corners.toList();
         listExtCorners.add(listOfPoints.get(0));
         listExtCorners.add(listOfPoints.get(3));
         listExtCorners.add(listOfPoints.get(20));
         listExtCorners.add(listOfPoints.get(23));
         MatOfPoint2f extCorners = new MatOfPoint2f();
         extCorners.fromList(listExtCorners);


         Mat rvec = new Mat(3,1,CV_32F);
         Mat tvec = new Mat(3,1,CV_32F);
         Calib3d.solvePnP(extCorner3D,extCorners,cameraMatrix,distCoeffsMat,rvec,tvec,true,Calib3d.CV_P3P);

         Mat rotation = new Mat(3,3,CV_32F);

         Calib3d.Rodrigues(rvec,rotation);


         solvePnpResult[0][0] = rotation.get(0,0)[0];
         solvePnpResult[0][1] = rotation.get(0,1)[0];
         solvePnpResult[0][2] = rotation.get(0,2)[0];
         solvePnpResult[0][3] = tvec.get(0,0)[0];

         solvePnpResult[1][0] = rotation.get(1,0)[0];
         solvePnpResult[1][1] = rotation.get(1,1)[0];
         solvePnpResult[1][2] = rotation.get(1,2)[0];
         solvePnpResult[1][3] = tvec.get(1,0)[0];

         solvePnpResult[2][0] = rotation.get(2,0)[0];
         solvePnpResult[2][1] = rotation.get(2,1)[0];
         solvePnpResult[2][2] = rotation.get(2,2)[0];
         solvePnpResult[2][3] = tvec.get(2,0)[0];

         solvePnpResult[3][0] = 0;
         solvePnpResult[3][1] = 0;
         solvePnpResult[3][2] = 0;
         solvePnpResult[3][3] = 1;

         for(int i = 0;i <pnpPose.length;i++) {
             pnpPose[i] = (float) solvePnpResult[i % 4][i / 4];
         }

/*
         float[] inverseMatrix = new float[16];
         float[] rotationTransposed = new float[16];
         float[] translationNegative = new float[16];

         Matrix.setIdentityM(inverseMatrix,0);
         Matrix.setIdentityM(rotationTransposed,0);
         Matrix.setIdentityM(translationNegative,0);

         Matrix.transposeM(inverseMatrix,0,pnpPose,0);

         for(int i = 0; i<9;i++){
             rotationTransposed[i + i/3] = inverseMatrix[i + i/3];
         }

         translationNegative[12] = -pnpPose[12];
         translationNegative[13] = -pnpPose[13];
         translationNegative[14] = -pnpPose[14];

         Matrix.multiplyMM(inverseMatrix,0,rotationTransposed,0,translationNegative,0);

         pnpPose = inverseMatrix;
         */
     }

         public static float[] getCameraTranslation(){
             if (solvePnpResult != null){
                 return new float[]{(float) -solvePnpResult[0][3],(float) solvePnpResult[1][3],
                         (float) solvePnpResult[2][3]};
             }else{
                 return null;
             }

         }

         public static float[] getSolvePnpPose(){

            if(pnpPose == null){
                return null;
            }else{
                float[] pnpPoseInverted = new float[16];
                Matrix.invertM(pnpPoseInverted,0,pnpPose,0);

                return pnpPoseInverted;

            }
         }




}
