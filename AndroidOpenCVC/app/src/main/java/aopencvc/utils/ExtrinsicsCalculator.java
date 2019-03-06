package aopencvc.utils;
import org.opencv.calib3d.Calib3d; //Solve pnp
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;



public class ExtrinsicsCalculator {


    MatOfPoint3f objectPoints;
    MatOfPoint2f imagePoints;
    Mat cameraMatrix; //Intrinsics
    MatOfDouble distCoeffs;

     public ExtrinsicsCalculator(){
         objectPoints = new MatOfPoint3f();
     }




     public Mat SolvePnp(Mat grayImage){

     }

     private calculateImagePoints(Mat image){
         MatOfPoint2f corners = new MatOfPoint2f();
         Calib3d.findChessboardCorners(image,new Size(4,6),corners);
         Calib3d.solvePnP(,corners,,,,);
     }


}
