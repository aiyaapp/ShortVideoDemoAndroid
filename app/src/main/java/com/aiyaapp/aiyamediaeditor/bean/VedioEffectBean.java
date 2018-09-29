package com.aiyaapp.aiyamediaeditor.bean;

import com.aiyaapp.aavt.gl.BaseFilter;

/**
 * Created by Administrator on 2018/2/1 0001.
 * 短视频特效bean
 */

public class VedioEffectBean {

    private String name;//特效名称
    private int icon; //特效图标
    private Class<? extends BaseFilter> mNowSvClazz; //特效类名
    private boolean isSelect;//是否选中


    public VedioEffectBean() {
    }

    public VedioEffectBean(String name, int icon, Class<? extends BaseFilter> mNowSvClazz) {
        this.name = name;
        this.icon = icon;
        this.mNowSvClazz = mNowSvClazz;
    }


    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends BaseFilter> getmNowSvClazz() {
        return mNowSvClazz;
    }

    public boolean isSelect() {
        return isSelect;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setmNowSvClazz(Class<? extends BaseFilter> mNowSvClazz) {
        this.mNowSvClazz = mNowSvClazz;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
