# 2018-tfm-Francisco-Palacios
## Week 1
To start I read the work of Eduardo Perdices: "Study of Convolutional Neural Networks using Keras Framework".

After that i installed the SD-SLAM packages and run it using my own camera to test how it worked.

Finally, I started an Android application. As this work will use SD-SLAM + AR in a mobile environment, I started to create an application for Android that uses C code (since SD-SLAM is written in C), adding OpenCV libraries that will probably be very helpful. In the video below, we can see the results of the final app, using the canny method through the OpenCV libraries.

### Canny example in android using OpenCV libraries:

[![](http://img.youtube.com/vi/IWV2fLG0j7k/0.jpg)](http://www.youtube.com/watch?v=IWV2fLG0j7k "")

### SD-SLAM Test:

[![](http://img.youtube.com/vi/L_nHDsnPDD0/0.jpg)](http://www.youtube.com/watch?v=L_nHDsnPDD0 "")

## Weeks 2-5

In these weeks i have implemented my first AR program for Android and i have also installed SD-SLAM for Android. These programs have been put into different activities along with the initial program that showed an example of OpenCV use. In the future I will be modifying this program and removing these functionalities, since they are not the objective of the work, but for the moment I will keep them in case they could be of help for someone.

The AR program consists of an object rendered with OpenGL (GLESv2) over the images provided by the camera mobile. We can see the results in the following video:

### An AR mobile example using GLESv2 and android:

[![](http://img.youtube.com/vi/ambrmh24XXo/0.jpg)](http://www.youtube.com/watch?v=ambrmh24XXo "")

To use SD-SLAM in mobile, I have used and followed as a guide the work of Eduardo Perdices that we can find in the following link: https://gitlab.jderobot.org/slam/slam-android.

In the next video, we can see how this program works:

### SD-SLAM in android:

[![](http://img.youtube.com/vi/shRlWhWcSqk/0.jpg)](http://www.youtube.com/watch?v=shRlWhWcSqk "")


## Weeks 6-8

In this period i tried to join SD-SLAM for Android and the AR program, with the objetive to use the calculations of SD-SLAM to perform the required changes in the AR program to seems that the object rendered is at a point of the space captured by the camera.

These changes consisted in using the translations and rotations provided by SD-SLAM and reflecting them in the AR camera. However, those changes didn't reflect well the movement of the object on the screen, so i tried to get a 3D point calculated by SD-SLAM and translate the AR object to it. This also, didn't reflect the movement well and the 3D point calculated appears out of the bounds of projection in the AR program.

Therefore, it will be necessary to make some calculations before transferring the data from SD-SLAM to the AR program. In the next video we can see the behaivor of this changes.

### SD-SLAM and AR in Android, first try:

[![](http://img.youtube.com/vi/cGTfwEcq_pI/0.jpg)](http://www.youtube.com/watch?v=cGTfwEcq_pI "")


