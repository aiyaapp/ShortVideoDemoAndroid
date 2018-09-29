package com.aiyaapp.aiyamediaeditor.vedio;

import com.aiyaapp.aavt.core.Renderer;
import com.aiyaapp.aavt.gl.BaseFilter;
import com.aiyaapp.aavt.gl.LazyFilter;
import com.aiyaapp.aiya.SluggardSvEffectTool;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class VedioEffectFlinger  implements Renderer{

    private SluggardSvEffectTool mSvTool = SluggardSvEffectTool.getInstance();
    private Class<? extends BaseFilter> mNowSvClazz;


    private LinkedList<Runnable> mTask = new LinkedList<>();
    private BaseFilter mShowFilter;

    public VedioEffectFlinger() {
        mShowFilter = new LazyFilter();
    }

    public void runOnRender(Runnable runnable) {
        mTask.add(runnable);
    }

    @Override
    public void create() {
        mShowFilter.create();
    }

    private int mWidth, mHeight;

    @Override
    public void sizeChanged(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        mShowFilter.sizeChanged(width, height);
    }

    @Override
    public void draw(int texture) {
        while (!mTask.isEmpty()) {
            mTask.removeFirst().run();
        }
        //短视频特效处理
        if (mNowSvClazz != null) {
            texture = mSvTool.processTexture(texture, mWidth, mHeight, mNowSvClazz);
        }
        mShowFilter.draw(texture);
    }

    @Override
    public void destroy() {
        mSvTool.onGlDestroy();
        mShowFilter.destroy();
    }

    public void onShortVideoEffectChanged(final Class<? extends BaseFilter> clazz) {
        runOnRender(new Runnable() {
            @Override
            public void run() {
                mNowSvClazz = clazz;
            }
        });
    }
}
