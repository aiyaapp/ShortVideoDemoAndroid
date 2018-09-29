package com.aiyaapp.aiyamediaeditor.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aiyaapp.aiyamediaeditor.vedio.VideoRender;
import com.aiyaapp.aiyamediaeditor_android.R;

import java.io.IOException;

public class VideoSurfaceActivity extends AppCompatActivity {


    private VideoRender mRenderer;
    private MediaPlayer mMediaPlayer;

    public static void newInstance(Activity act, String vedioPath) {
        Intent intent = new Intent(act, VideoSurfaceActivity.class);
        intent.putExtra("VEDIOPATH", vedioPath);
        act.startActivity(intent);
    }



    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_surface);

        initViews();
    }

    private void initViews() {
        String path = getIntent().getStringExtra("VEDIOPATH");
        glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceview);
        glSurfaceView.setEGLContextClientVersion(2);
        mRenderer = new VideoRender(this);


        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setMediaPlayer(mMediaPlayer);
            }
        });
    }
}
