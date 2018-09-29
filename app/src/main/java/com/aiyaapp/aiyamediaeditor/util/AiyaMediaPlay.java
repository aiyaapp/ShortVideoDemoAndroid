package com.aiyaapp.aiyamediaeditor.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 哎吖视频播放
 */

public class AiyaMediaPlay {


    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private String path;

    public AiyaMediaPlay(SurfaceView surfaceView, String path) {
        this.surfaceView = surfaceView;
        this.path = path;
        init();
    }


    public void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.seekTo(0);
                mediaPlayer.setLooping(true);
            }
        });


        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//4.0一下的版本需要加该段代码。
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                /**
                 * 当点击手机上home键（或其他使SurfaceView视图消失的键）时，调用该方法，获取到当前视频的播放值，currentPosition。
                 * 并停止播放。
                 */
                stop();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.seekTo(0);
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void play() {
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置视频流类型
        try {

            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void rePlay(String path) {
        this.path = path;
        if (mediaPlayer != null) {
            play();
        }
    }

    public void onDestory() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

}
