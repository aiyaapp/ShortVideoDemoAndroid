package com.aiyaapp.aiyamediaeditor.ui;

import android.widget.ImageView;

import com.aiyaapp.aiyamediaeditor.view.GlideCircleTransform;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/26 0026.
 * 滤镜
 */

public class MirrorAdapter extends BaseQuickAdapter<MirrorBean> {

    public MirrorAdapter() {
        super(R.layout.item_mirror, null);
        initEirrorList();
        setNewData(datas);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MirrorBean mirrorBean) {
        ImageView imageView = baseViewHolder.getView(R.id.mirror_icon);
        imageView.setSelected(mirrorBean.isSelect);
        Glide.with(mContext).load(mirrorBean.icon).transform(new GlideCircleTransform(mContext)).into(imageView);
        baseViewHolder.setText(R.id.mirror_name, mirrorBean.name);
    }


    private List<MirrorBean> datas;

    private void initEirrorList() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.add(new MirrorBean("无", R.mipmap.default_mirror_icon, true));
        datas.add(new MirrorBean("滤镜一", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜二", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜三", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜四", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜五", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜六", R.mipmap.default_mirror_icon, false));
        datas.add(new MirrorBean("滤镜七", R.mipmap.default_mirror_icon, false));
    }


    public void switchPosition(int position) {
        if (datas == null || datas.size() <= 0) return;
        for (int i = 0; i < datas.size(); i++) {
            if (i == position) {
                datas.get(i).isSelect = true;
            } else {
                datas.get(i).isSelect = false;
            }
            this.notifyDataSetChanged();
        }
    }
}
