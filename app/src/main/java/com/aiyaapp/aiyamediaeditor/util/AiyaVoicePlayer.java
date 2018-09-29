package com.aiyaapp.aiyamediaeditor.util;

import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class AiyaVoicePlayer {

    private final MediaPlayer voicePlayer;
    private String voiceUrl; //音频的地址

    public AiyaVoicePlayer() {
        voicePlayer = new MediaPlayer();
        voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                voicePlayer.start();
                mediaPlayer.setLooping(true);
            }
        });

    }


    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    /**
     * 开始播放
     */
    public void play() {
        if (TextUtils.isEmpty(voiceUrl) || !new File(voiceUrl).exists()) {
            return;
        }
        try {
            voicePlayer.reset();
            voicePlayer.setDataSource(voiceUrl);
            voicePlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音量
     *
     * @param voice
     */
    public void setVolume(float voice) {
        if (voicePlayer != null) {
            voicePlayer.setVolume(voice, voice);
        }
    }


    /**
     * 停止播放
     */
    public void stop() {
        if (voicePlayer != null) {
            voicePlayer.stop();
        }
    }


}
