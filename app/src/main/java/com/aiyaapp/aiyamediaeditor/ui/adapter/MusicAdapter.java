package com.aiyaapp.aiyamediaeditor.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiyaapp.aiyamediaeditor.bean.MusicBean;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class MusicAdapter extends BaseQuickAdapter<MusicBean> {


    public MusicAdapter() {
        super(R.layout.music_item_layout, null);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MusicBean musicBean) {
        ImageView select_view = baseViewHolder.getView(R.id.select_view);
        TextView music_name = baseViewHolder.getView(R.id.music_name);
        music_name.setText(musicBean.fileName);
        if (musicBean.isSelect) {
            select_view.setVisibility(View.VISIBLE);
            music_name.setSelected(true);
        } else {
            select_view.setVisibility(View.GONE);
            music_name.setSelected(false);
        }
        TextView time_view = baseViewHolder.getView(R.id.time_view);
        time_view.setText(convertTime(musicBean.duration));
        if (musicBean.duration == 0) {
            time_view.setVisibility(View.GONE);
        }

    }


    public void setDatas(List<MusicBean> list) {
        if (list == null || list.size() <= 0) return;
        MusicBean musicBean = new MusicBean();
        musicBean.fileName = "无";
        musicBean.duration = 0;
        musicBean.isSelect = true;
        list.add(0, musicBean);
        setNewData(list);

    }


    public void switchPosition(int position) {
        for (int i = 0; i < mData.size(); i++) {
            if (i == position) {
                mData.get(i).isSelect = true;
            } else {
                mData.get(i).isSelect = false;
            }
            this.notifyDataSetChanged();
        }
    }

    /**
     * 格式化时间
     *
     * @param sec
     * @return
     */
    public static String convertTime(long sec) {
        sec = sec / 1000;
        if (sec < 0)
            return String.valueOf(sec);

        StringBuffer buf = new StringBuffer();
        if (sec > 60) {
            int min = (int) (sec / 60);
            int second = (int) (sec % 60);
            buf.append(min).append(":").append(second);
        } else {
            buf.append(sec);
        }

        return buf.toString();
    }
}