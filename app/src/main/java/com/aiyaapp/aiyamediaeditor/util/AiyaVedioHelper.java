package com.aiyaapp.aiyamediaeditor.util;

import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aiyaapp.aavt.av.Mp4Processor2;
import com.aiyaapp.aavt.gl.BaseFilter;
import com.aiyaapp.aiyamediaeditor.vedio.VedioEffectFlinger;

/**
 * Created by Administrator on 2018/2/1 0001.
 * 哎吖短视频特效处理工具类
 */

public class AiyaVedioHelper {

    private SurfaceView surfaceView;
    private String path;
    private Mp4Processor2 mMp4Processor;
    private VedioEffectFlinger mFlinger;

    public AiyaVedioHelper(SurfaceView surfaceView, String path) {
        this.surfaceView = surfaceView;
        this.path = path;
        init();
    }


    private String tempPath;

    private void init() {
        if (TextUtils.isEmpty(path)) return;
//        tempPath = path.replace("record.mp4", "record_e.mp4");
        mMp4Processor = new Mp4Processor2();
        mMp4Processor.setInputPath(path);
//        mMp4Processor.setOutputPath(tempPath);
        mMp4Processor.setOpenAudio(false);
        mFlinger = new VedioEffectFlinger();
        mMp4Processor.setRenderer(mFlinger);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mMp4Processor.setSurface(holder);
                mMp4Processor.setPreviewSize(width, height);
                mMp4Processor.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mMp4Processor.stopPreview();
            }
        });
    }

    /**
     * 开始编辑
     */
    public void startEdit(Class<? extends BaseFilter> clazz) {
        try {
            if (mFlinger != null) {
                mFlinger.onShortVideoEffectChanged(clazz);
            }
            if (mMp4Processor != null) {
//                mMp4Processor.startRecord();
                mMp4Processor.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 暂停编辑
     */
    public void stopEdit() {
        if (mMp4Processor != null) {
            mMp4Processor.stopRecord();
            mMp4Processor.close();
        }
    }



}
