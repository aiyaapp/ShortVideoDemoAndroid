package com.aiyaapp.aiyamediaeditor.ui.adapter;

import android.app.Activity;
import android.util.JsonReader;
import android.widget.ImageView;

import com.aiyaapp.aiyamediaeditor.bean.EffectBean;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/25 0025.
 * 人脸特效
 */

public class EffectAdapter extends BaseQuickAdapter<EffectBean> {

    private Activity act;

    public EffectAdapter(Activity activity) {
        super(R.layout.item_image, null);
        this.act = activity;
        setNewData(initMenuData("modelsticker/stickers.json"));

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, EffectBean effectBean) {
        ImageView imageView = baseViewHolder.getView(R.id.mImage);
        imageView.setSelected(effectBean.isSelect);
        imageView.setImageResource(effectBean.icon);
        baseViewHolder.setText(R.id.effect_name, effectBean.name);
    }


    public int[] effectIcons = new int[]{
            R.mipmap.icon_no, R.mipmap.baowener, R.mipmap.gougou, R.mipmap.maorong, R.mipmap.tuer, R.mipmap.shiwaitaoyuan,
            R.mipmap.xiaohongmao
            , R.mipmap.arg
    };
    private ArrayList<EffectBean> mEffectList;

    //初始化特效按钮菜单
    public ArrayList<EffectBean> initMenuData(String menuPath) {
        if (mEffectList == null) {
            mEffectList = new ArrayList<>();
        }
        try {
            JsonReader r = new JsonReader(new InputStreamReader(act.getAssets().open(menuPath)));
            r.beginArray();
            while (r.hasNext()) {
                EffectBean menu = new EffectBean();
                r.beginObject();
                String name;
                while (r.hasNext()) {
                    name = r.nextName();
                    if (name.equals("name")) {
                        menu.name = r.nextString();
                    } else if (name.equals("path")) {
                        menu.path = r.nextString();
                    }
                }
                mEffectList.add(menu);
                r.endObject();
            }
            r.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < effectIcons.length && i < mEffectList.size(); i++) {
            if (i == 0) {
                mEffectList.get(i).isSelect = true;
            }
            mEffectList.get(i).icon = effectIcons[i];
        }

        return mEffectList;
    }


    public void switchPosition(int position) {
        if (mEffectList == null || mEffectList.size() <= 0) return;
        for (int i = 0; i < mEffectList.size(); i++) {
            if (i == position) {
                mEffectList.get(i).isSelect = true;
            } else {
                mEffectList.get(i).isSelect = false;
            }
            this.notifyDataSetChanged();
        }
    }

}
