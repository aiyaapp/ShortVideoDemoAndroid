package com.aiyaapp.aiyamediaeditor.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/7 0007.
 * 音乐bean
 */

public class MusicBean implements Serializable {
    private static final long serialVersionUID = 2178420052691000209L;

    public long id;
    public String fileName;
    public String title;
    public int duration;
    public String artist;
    public String album;
    public String year;
    public String mime;
    public String size;
    public String fileUrl;

    public boolean isSelect = false;

    public boolean isPlaying;

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return obj instanceof MusicBean && ((MusicBean) obj).id == this.id;
    }
}
