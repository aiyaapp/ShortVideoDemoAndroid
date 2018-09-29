package com.aiyaapp.aiyamediaeditor.ui;


import android.content.Context;

import com.aiyaapp.aavt.core.Renderer;
import com.aiyaapp.aavt.gl.BaseFilter;
import com.aiyaapp.aavt.gl.LazyFilter;
import com.aiyaapp.aiya.AiyaBeauty;
import com.aiyaapp.aiya.AiyaTracker;
import com.aiyaapp.aiya.filter.AyBeautyFilter;
import com.aiyaapp.aiya.filter.AyBigEyeFilter;
import com.aiyaapp.aiya.filter.AyTrackFilter;
import com.aiyaapp.aiya.render.AiyaGiftFilter;

import java.util.LinkedList;

public class DefaultEffectFlinger implements Renderer {

    private Context mContext;
    private BaseFilter mShowFilter;
    private AyTrackFilter mTrackFilter;
    private final AiyaGiftFilter mEffectFilter;
    private final AyBeautyFilter mAiyaBeautyFilter;//美颜
    private final AyBigEyeFilter mAiyaBigEyeFilter;

    public DefaultEffectFlinger(Context context) {
        this.mContext = context;
        mTrackFilter = new AyTrackFilter(context);
        mEffectFilter = new AiyaGiftFilter(mContext, null);
        mShowFilter = new LazyFilter();
        mAiyaBeautyFilter = new AyBeautyFilter(AiyaBeauty.TYPE2);
        mAiyaBigEyeFilter = new AyBigEyeFilter();
    }

    private LinkedList<Runnable> mTask = new LinkedList<>();

    public void runOnRender(Runnable runnable) {
        mTask.add(runnable);
    }

    @Override
    public void create() {
        mTrackFilter.create();
        mAiyaBigEyeFilter.create();
        mEffectFilter.create();
        mShowFilter.create();
        mAiyaBeautyFilter.create();
    }


    /**
     * 设置特效
     */
    public void setEffect(final String path) {
        runOnRender(new Runnable() {
            @Override
            public void run() {
                mEffectFilter.setEffect(path);
            }
        });
    }

    private boolean openBeauty = false;

    public void setIsOpenBeauty(boolean isOpenBeauty) {
        this.openBeauty = isOpenBeauty;
    }

    @Override
    public void sizeChanged(int width, int height) {
        mTrackFilter.sizeChanged(width, height);
        mEffectFilter.sizeChanged(width, height);
        mShowFilter.sizeChanged(width, height);
        mAiyaBeautyFilter.sizeChanged(width, height);
        mAiyaBigEyeFilter.sizeChanged(width, height);
        mAiyaBigEyeFilter.setDegree(0.5f);
    }

    @Override
    public void draw(int texture) {
        while (!mTask.isEmpty()) {
            mTask.removeFirst().run();
        }
        mTrackFilter.drawToTexture(texture);

        if (mEffectFilter != null) {
            mEffectFilter.setFaceDataID(mTrackFilter.getFaceDataID());
            texture = mEffectFilter.drawToTexture(texture);
        }
        if (mAiyaBeautyFilter != null && openBeauty) { //美颜处理
            texture = mAiyaBeautyFilter.drawToTexture(texture);
        }
        if(mAiyaBigEyeFilter != null){ // big eye
            mAiyaBigEyeFilter.setFaceDataID(mTrackFilter.getFaceDataID());
            texture = mAiyaBigEyeFilter.drawToTexture(texture);
        }
        mShowFilter.draw(texture);
    }

    @Override
    public void destroy() {
        mTrackFilter.destroy();
        mEffectFilter.destroy();
        mAiyaBeautyFilter.destroy();
        mShowFilter.destroy();
        mAiyaBigEyeFilter.destroy();
    }

    public void release() {
        if (mEffectFilter != null) {
            mEffectFilter.release();
        }
    }
}
