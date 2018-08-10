## Some instructions

I used Android Studio to create and build this project. Make sure you have installed Android SDK with NDK and CMake.

You need to change some absolute and relative paths:

- Remplace the symlink jniLibs in ./app/src/main for another symlink pointing to YOUR_OPENCV_SDK/sdk/native/libs

- In ./app/CMakeLists.txt change the directory in line 9 to your YOUR_OPENCV_SDK/sdk/native/jni/include


