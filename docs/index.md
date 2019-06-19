---
layout: default
---

## Table of contents

- [Week 1 - Testing SD-SLAM and first mobile app](#week1)
- [Weeks 2-5 - Testing GLESv2 AR and SD-SLAM in a mobile app](#week2)
- [Weeks 6-8 - Integrating SD-SLAM and GLESv2 AR together](#week3)
- [Weeks 9-11 - Improving the AR app](#week4)
- [Weeks 12-13 - Pattern initialization and resolution reduction](#week5)
- [Weeks 14-18 - Adding some new features](#week6)
- [Weeks 19-24 - Solving problems: SelfRecorder, CamTrail and Arrows](#week7)
## References
The contents of android-AccelerometerPlay are from https://github.com/googlesamples/android-AccelerometerPlay/.

<a name="week7"></a>
## Weeks 19-24

### #In progress
It's been a while since my last report. In this time, i found some problems with the functionalities implemented in the program.

- SelfRecorder: Due to the characteristics of the device in which I do the tests, make a recording of what the app shows becomes very tedious, due to the consumption of resources that the app + self recordings do. So I've created a half way method that stores the trajectory followed during a test. With that I intend to alleviate the overload and still have something to show.

- CamTrail: Until now the camera trail was static, every time the ACUTAL pose was modified, its position was saved in a list that was later rendered with GLES. This presented some problems when using it, often creating errors in how it was represented. I have modified this, so that what is rendered is the positions of the KeyFrames collected by SD-SLAM. This will cause this trail to be dynamic and change according to the information collected by SD-SLAM and making it more precise.
### #mysterious symbol??

- Arrows: I thought I was done fighting with this functionality. Nothing further. It seems that it still had problems in locating exactly where to represent the arrows and their direction.

In the near future, i also will be working on including the IMU sensor values to help SD-SLAM with the localization task.


<a name="week6"></a>
## Weeks 14-18

Since my last update, i did a little changes in the code to clean the draw functions, creating 2 new classes for this task (CoordsObject and InitShaders). I also increased the resolution of the openGL renders, since it seems to don't reduce the performance and it look better.

I also create an Arrow class with the purpose of try to create a path between 2 points using the arrows as directions that i must follow to go from point A to point B. I'm working currently in this features and i'm having some issues drawing more than 1 object.

Finally, i create a new class (Recorder) to record the tablet screen i'm testing with. I was using an app to do this job all this time, so since i'm using it a lot, I decided to program a recorder. I need configurate some of the variables, to try increase the performance, but it works pretty well. We can se an example in the video below. Also in the video, there is an example of the relocalization of SD_SLAM.

### SD-SLAM and AR in Android, Pattern initialization and resolution reduction:

[![](http://img.youtube.com/vi/xx2N_K1n-pw/0.jpg)](https://www.youtube.com/watch?v=xx2N_K1n-pw "")

<a name="week5"></a>
## Weeks 12-13

In these weeks I have changed the initialization from SD-SLAM to pattern initialization. To do this, it was necessary to create a couple of "set" functions in the SD-SLAM code so it would be easier to access the variables that choose the type of initialization as well as the size of the cells of the pattern.

In addition, I have also reduced the resolution of the images supplied to SD-SLAM which has meant an increase in program efficiency and quality. It has increased both the frame rate and the data measurement. As we can see in the following video, the rendered object no longer seems to float as much as in previous versions.

I have also created a function that renders a line as the wake of the camera positions. We can see it in the second video.

### SD-SLAM and AR in Android, Pattern initialization and resolution reduction:

[![]http://img.youtube.com/vi/BQ4DcIcNh6k/0.jpg](https://www.youtube.com/watch?v=BQ4DcIcNh6k "")

### SD-SLAM and AR in Android, CamTrail:

[![]http://img.youtube.com/vi/062dlFRNw3s/0.jpg](https://www.youtube.com/watch?v=062dlFRNw3s "")

<a name="week4"></a>
## Weeks 9-11

During this weeks i tried to improve the behavior of the AR camera, i calculated a plane using the points provided by SD-SLAM to put the grid of the AR object in it. I had some issues with this calculations because it seems that the Y and Z coords of SD-SLAM are in the opposite direction to the Y and Z coords of GLESv2 (the library controling the AR camera).

So i needed to rotate 180º along the x-axis and with this i made some mistakes trying to create the appropriated roation matrix, but they are all solved.

I also changed the projection matrix used by the AR camera using the calibration parameters of the camera that i am using to make the tests of the code.

And finally i included a small dot in the up left corner of the images provided by SD-SLAM to know what is the state of the tracker (green -> not initialized, blue -> ok, red -> lost) and that the tests are simpler.

The results are not yet what is expected, because de point calculated to translate the AR object seems to be floating and not close to the flat surface on which I am trying to initialize it.

In the following video we can see better what I'm trying to say.

### SD-SLAM and AR in Android, second try including plane calculation and calibration parameters:

[![]http://img.youtube.com/vi/rmWQLTqQMNg/0.jpg](http://www.youtube.com/watch?v=rmWQLTqQMNg "")


<a name="week3"></a>
## Weeks 6-8

In this period i tried to join SD-SLAM for Android and the AR program, with the objetive to use the calculations of SD-SLAM to perform the required changes in the AR program to seems that the object rendered is at a point of the space captured by the camera.

These changes consisted in using the translations and rotations provided by SD-SLAM and reflecting them in the AR camera. However, those changes didn't reflect well the movement of the object on the screen, so i tried to get a 3D point calculated by SD-SLAM and translate the AR object to it. This also, didn't reflect the movement well and the 3D point calculated appears out of the bounds of projection in the AR program.

Therefore, it will be necessary to make some calculations before transferring the data from SD-SLAM to the AR program. In the next video we can see the behaivor of this changes.

### SD-SLAM and AR in Android, first try:

[![]http://img.youtube.com/vi/cGTfwEcq_pI/0.jpg](http://www.youtube.com/watch?v=cGTfwEcq_pI "")


<a name="week2"></a>
## Weeks 2-5

In these weeks i have implemented my first AR program for Android and i have also installed SD-SLAM for Android. These programs have been put into different activities along with the initial program that showed an example of OpenCV use. In the future I will be modifying this program and removing these functionalities, since they are not the objective of the work, but for the moment I will keep them in case they could be of help for someone.

The AR program consists of an object rendered with OpenGL (GLESv2) over the images provided by the camera mobile. We can see the results in the following video:

### An AR mobile example using GLESv2 and android:

[![]http://img.youtube.com/vi/ambrmh24XXo/0.jpg](http://www.youtube.com/watch?v=ambrmh24XXo "")

To use SD-SLAM in mobile, I have used and followed as a guide the work of Eduardo Perdices that we can find in the following link: https://gitlab.jderobot.org/slam/slam-android.

In the next video, we can see how this program works:

### SD-SLAM in android:


[![]http://img.youtube.com/vi/shRlWhWcSqk/0.jpg](http://www.youtube.com/watch?v=shRlWhWcSqk "")

<a name="week1"></a>
## Week 1
To start I read the work of Eduardo Perdices: "Techniques for robust visual localization
of real-time robots with and without maps" (https://gsyc.urjc.es/jmplaza/pfcs/phd-eduardo_perdices-2017.pdf).

After that i installed the SD-SLAM packages and run it using my own camera to test how it worked.

Finally, I started an Android application. As this work will use SD-SLAM + AR in a mobile environment, I started to create an application for Android that uses C code (since SD-SLAM is written in C), adding OpenCV libraries that will probably be very helpful. In the video below, we can see the results of the final app, using the canny method through the OpenCV libraries.

### Canny example in android using OpenCV libraries:

[![]http://img.youtube.com/vi/IWV2fLG0j7k/0.jpg](http://www.youtube.com/watch?v=IWV2fLG0j7k "")

### SD-SLAM Test:

[![]http://img.youtube.com/vi/L_nHDsnPDD0/0.jpg](http://www.youtube.com/watch?v=L_nHDsnPDD0 "")
