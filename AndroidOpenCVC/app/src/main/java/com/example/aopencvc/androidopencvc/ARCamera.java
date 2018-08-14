package com.example.aopencvc.androidopencvc;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import aopencvc.opengl.SurfaceViewer;


public class ARCamera extends AppCompatActivity {


    private SurfaceViewer mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_arcamera);
        mGLView = (SurfaceViewer) findViewById(R.id.surface_opengl);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}
