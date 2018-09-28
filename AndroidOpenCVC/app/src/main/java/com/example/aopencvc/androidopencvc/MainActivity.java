package com.example.aopencvc.androidopencvc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("opencv_java3");
        System.loadLibrary("SD_SLAM");
        System.loadLibrary("SLAM");

    }


    public void launchAR(View view){
        Intent intent = new Intent(this,ARCamera.class);
        startActivity(intent);

    }

}
