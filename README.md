# 2018-tfm-Francisco-Palacios

## Table of contents

- [Week 1](#heading-3)
- [Weeks 2-5](#heading-2)
- [Weeks 6-8](#heading-1)
- [Weeks 9-11](#week9-11)




<a name="week9-11"></a>
## Weeks 9-11

During this weeks i tried to improve the behavior of the AR camera, i calculated a plane using the points provided by SD-SLAM to put the grid of the AR object in it. I had some issues with this calculations because it seems that the Y and Z coords of SD-SLAM are in the opposite direction to the Y and Z coords of GLESv2 (the library controling the AR camera).

So i needed to rotate 180º along the x-axis and with this i made some mistakes trying to create the appropriated roation matrix, but they are all solved.

I also changed the projection matrix used by the AR camera using the calibration parameters of the camera that i am using to make the tests of the code.

And finally i included a small dot in the up left corner of the images provided by SD-SLAM to know what is the state of the tracker (green -> not initialized, blue -> ok, red -> lost) and that the tests are simpler.

The results are not yet what is expected, because de point calculated to translate the AR object seems to be floating and not close to the flat surface on which I am trying to initialize it

In the following video we can see better what I'm trying to say.

### SD-SLAM and AR in Android, second try including plane calculation and calibration parameters:

[![](http://img.youtube.com/vi/rmWQLTqQMNg/0.jpg)](http://www.youtube.com/watch?v=rmWQLTqQMNg "")

## Weeks 6-8

In this period i tried to join SD-SLAM for Android and the AR program, with the objetive to use the calculations of SD-SLAM to perform the required changes in the AR program to seems that the object rendered is at a point of the space captured by the camera.

These changes consisted in using the translations and rotations provided by SD-SLAM and reflecting them in the AR camera. However, those changes didn't reflect well the movement of the object on the screen, so i tried to get a 3D point calculated by SD-SLAM and translate the AR object to it. This also, didn't reflect the movement well and the 3D point calculated appears out of the bounds of projection in the AR program.

Therefore, it will be necessary to make some calculations before transferring the data from SD-SLAM to the AR program. In the next video we can see the behaivor of this changes.

### SD-SLAM and AR in Android, first try:

[![](http://img.youtube.com/vi/cGTfwEcq_pI/0.jpg)](http://www.youtube.com/watch?v=cGTfwEcq_pI "")


## Weeks 2-5

In these weeks i have implemented my first AR program for Android and i have also installed SD-SLAM for Android. These programs have been put into different activities along with the initial program that showed an example of OpenCV use. In the future I will be modifying this program and removing these functionalities, since they are not the objective of the work, but for the moment I will keep them in case they could be of help for someone.

The AR program consists of an object rendered with OpenGL (GLESv2) over the images provided by the camera mobile. We can see the results in the following video:

### An AR mobile example using GLESv2 and android:

[![](http://img.youtube.com/vi/ambrmh24XXo/0.jpg)](http://www.youtube.com/watch?v=ambrmh24XXo "")

To use SD-SLAM in mobile, I have used and followed as a guide the work of Eduardo Perdices that we can find in the following link: https://gitlab.jderobot.org/slam/slam-android.

In the next video, we can see how this program works:

### SD-SLAM in android:

[![](http://img.youtube.com/vi/shRlWhWcSqk/0.jpg)](http://www.youtube.com/watch?v=shRlWhWcSqk "")


## Week 1
To start I read the work of Eduardo Perdices: "Study of Convolutional Neural Networks using Keras Framework".

After that i installed the SD-SLAM packages and run it using my own camera to test how it worked.

Finally, I started an Android application. As this work will use SD-SLAM + AR in a mobile environment, I started to create an application for Android that uses C code (since SD-SLAM is written in C), adding OpenCV libraries that will probably be very helpful. In the video below, we can see the results of the final app, using the canny method through the OpenCV libraries.

### Canny example in android using OpenCV libraries:

[![](http://img.youtube.com/vi/IWV2fLG0j7k/0.jpg)](http://www.youtube.com/watch?v=IWV2fLG0j7k "")

### SD-SLAM Test:

[![](http://img.youtube.com/vi/L_nHDsnPDD0/0.jpg)](http://www.youtube.com/watch?v=L_nHDsnPDD0 "")

