package com.aiyaapp.aiyamediaeditor.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aiyaapp.aiyamediaeditor.util.AiyaMediaPlay;
import com.aiyaapp.aiyamediaeditor.util.AiyaVoicePlayer;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.piterwilson.audio.MP3RadioStreamPlayer;
import com.aiya.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;

/**
 * 视频编辑页面
 */
public class VedioEditActivity extends AppCompatActivity implements View.OnClickListener {


    private AiyaMediaPlay aiyaMediaPlay;
    private View audio_voice_layout, content_layout, vedio_voice_layout, watermark_layout, clip_music_layout;
    private TextView vedio_voice_view;
    private AudioWaveView audioWave;


    public static void newInstance(Activity act, String vedioPath) {
        Intent intent = new Intent(act, VedioEditActivity.class);
        intent.putExtra("VEDIOPATH", vedioPath);
        act.startActivity(intent);
    }


    private SurfaceView surfaceView;
    private String vedioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_edit);
        initViews();
        initData();
    }

    private void initViews() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        audio_voice_layout = findViewById(R.id.audio_voice_layout);
        content_layout = findViewById(R.id.content_layout);
        vedio_voice_layout = findViewById(R.id.vedio_voice_layout);
        watermark_layout = findViewById(R.id.watermark_layout);
        clip_music_layout = findViewById(R.id.clip_music_layout);
        audioWave = (AudioWaveView) findViewById(R.id.audioWave);
        vedio_voice_view = (TextView) findViewById(R.id.vedio_voice_view);
        initSeekBar();
        findViewById(R.id.effect_layout).setOnClickListener(this);
        findViewById(R.id.music_layout).setOnClickListener(this);
        findViewById(R.id.audio_layout).setOnClickListener(this);
        findViewById(R.id.root_layout).setOnClickListener(this);
        findViewById(R.id.clip_music).setOnClickListener(this);
        findViewById(R.id.return_view).setOnClickListener(this);
        vedio_voice_layout.setOnClickListener(this);
        watermark_layout.setOnClickListener(this);
    }


    private SeekBar voice_seekbar, music_seekbar;

    private void initSeekBar() {
        voice_seekbar = (SeekBar) findViewById(R.id.voice_seekbar);
        music_seekbar = (SeekBar) findViewById(R.id.music_seekbar);
        voice_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (voicePlayer != null) {
                    voicePlayer.setVolume(progress / 100.0f);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        music_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayer != null) {
                    musicPlayer.setVolume(progress / 100.0f);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public boolean openVoice = false;
    public boolean selectVoice = false;
    public boolean iswatermark = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clip_music://裁剪音乐
                clip_music_layout.setVisibility(View.VISIBLE);
                content_layout.setVisibility(View.GONE);
                clipMusic();
                break;
            case R.id.watermark_layout://打开水印
                iswatermark = !iswatermark;
                watermark_layout.setSelected(iswatermark);
                break;
            case R.id.vedio_voice_layout://原音开关
                openVoice = !openVoice;
                vedio_voice_layout.setSelected(openVoice);
                if (openVoice) {
                    vedio_voice_view.setText("原音开");
                } else {
                    vedio_voice_view.setText("原音关");
                }
                playVoice(openVoice);
                break;
            case R.id.effect_layout://特效编辑
                EffectEditActivity.newInstance(this, vedioPath);
                break;
            case R.id.music_layout://选择音乐
                SelectMusioActivity.newInstance(this);
                break;
            case R.id.audio_layout://声音开关
                selectVoice = !selectVoice;
                if (selectVoice) {
                    audio_voice_layout.setVisibility(View.VISIBLE);
                    content_layout.setVisibility(View.GONE);
                } else {
                    audio_voice_layout.setVisibility(View.GONE);
                    content_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.root_layout:
                selectVoice = false;
                audio_voice_layout.setVisibility(View.GONE);
                clip_music_layout.setVisibility(View.GONE);
                content_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.return_view:
                this.finish();
                break;
        }
    }


    private String musicUrl;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {//选取音乐返回
                case SelectMusioActivity.REQUEST_MUSIC_CODE:
                    musicUrl = data.getStringExtra("MUSICURL");
                    System.out.println("musicUrl ==" + musicUrl);
                    playMusic();
                    break;
            }
        }
    }
    private AiyaVoicePlayer musicPlayer;
    /**
     * 播放背景音乐
     */
    private void playMusic() {
        if (TextUtils.isEmpty(musicUrl) || !new File(musicUrl).exists()) {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
            return;
        }
        if (musicPlayer == null) {
            musicPlayer = new AiyaVoicePlayer();
        } else {
            musicPlayer.stop();
        }
        musicPlayer.setVoiceUrl(musicUrl);
        musicPlayer.play();
    }

    private MP3RadioStreamPlayer player;

    /**
     * 裁剪音乐
     */
    private void clipMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        player = new MP3RadioStreamPlayer();
        player.setUrlString(musicUrl);
        int size = getScreenWidth(this) / dip2px(this, 1);//控件默认的间隔是1
        player.setDataList(audioWave.getRecList(), size);
        audioWave.setBaseRecorder(player);
        audioWave.startView();
        try {
            player.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);//
        return outMetrics.widthPixels;
    }

    /**
     * dip转为PX
     */
    public static int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }

    /**
     * 播放原音
     */
    private AiyaVoicePlayer voicePlayer;

    private void playVoice(boolean isPlay) {
        String voicePath = vedioPath.replace(".mp4", ".aac");
        if (TextUtils.isEmpty(voicePath) || !new File(voicePath).exists()) {
            return;
        }
        if (voicePlayer == null) {
            voicePlayer = new AiyaVoicePlayer();
            voicePlayer.setVoiceUrl(voicePath);
        }
        if (isPlay) {
            voicePlayer.play();
        } else {
            voicePlayer.stop();
        }

    }

    //-------------------------------视频播放 开始---------------------------------------------
    private void initData() {
        vedioPath = getIntent().getStringExtra("VEDIOPATH");
        if (TextUtils.isEmpty(vedioPath)) {
            this.finish();
            return;
        }
        aiyaMediaPlay = new AiyaMediaPlay(surfaceView, vedioPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aiyaMediaPlay != null) {
            aiyaMediaPlay.onDestory();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (aiyaMediaPlay != null) {
            aiyaMediaPlay.stop();
        }
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        if (voicePlayer != null) {
            voicePlayer.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (aiyaMediaPlay != null) {
            aiyaMediaPlay.pause();
        }
    }
    //-------------------------------视频播放 结束---------------------------------------------
}
