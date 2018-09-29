package com.aiyaapp.aiyamediaeditor.util;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class MediaUtils {

    /**
     * 该音频是否符合采样率是sampleRate,通道数是channelCount,值为-1表示忽略该条件
     *
     * @param audioFile
     * @param sampleRate
     * @param channelCount
     * @return
     */
    public static boolean isMatchAudioFormat(String audioFile, int sampleRate,int channelCount){
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(audioFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);

        boolean result = true;
        if(sampleRate != -1){
            result = sampleRate == mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        }

        if(result && channelCount != -1){
            result = channelCount == mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        }

        mex.release();

        return result;
    }

}
