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

    }


    public void launchCanny(View view){
        Intent intent = new Intent(this,Canny.class);
        startActivity(intent);
    }

    public void launchOpenGL(View view){
        Intent intent = new Intent(this,ARCamera.class);
        startActivity(intent);

    }

    public void launchSDSLAM(View view) {
        Intent intent = new Intent(this,SDSLAM.class);
        startActivity(intent);
    }

}
