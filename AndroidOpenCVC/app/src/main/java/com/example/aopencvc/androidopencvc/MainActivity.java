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

    private boolean record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("opencv_java3");
        System.loadLibrary("SD_SLAM");
        System.loadLibrary("SLAM");
        record = false;

    }


    public void launchAR(View view){
        Intent intent = new Intent(this,ARCamera.class);
        Bundle params = new Bundle();
        params.putBoolean("recording", record);
        intent.putExtras(params);
        startActivity(intent);

    }

    public void record(View view){
        record = !record;
    }

}
