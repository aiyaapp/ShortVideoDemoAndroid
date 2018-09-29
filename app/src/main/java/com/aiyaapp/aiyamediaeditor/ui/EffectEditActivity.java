package com.aiyaapp.aiyamediaeditor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.aiyaapp.aavt.gl.LazyFilter;
import com.aiyaapp.aiya.filter.SvBlackMagicFilter;
import com.aiyaapp.aiya.filter.SvBlackWhiteTwinkleFilter;
import com.aiyaapp.aiya.filter.SvCutSceneFilter;
import com.aiyaapp.aiya.filter.SvDysphoriaFilter;
import com.aiyaapp.aiya.filter.SvFinalZeligFilter;
import com.aiyaapp.aiya.filter.SvFluorescenceFilter;
import com.aiyaapp.aiya.filter.SvFourScreenFilter;
import com.aiyaapp.aiya.filter.SvHallucinationFilter;
import com.aiyaapp.aiya.filter.SvRollUpFilter;
import com.aiyaapp.aiya.filter.SvSeventysFilter;
import com.aiyaapp.aiya.filter.SvShakeFilter;
import com.aiyaapp.aiya.filter.SvSpiritFreedFilter;
import com.aiyaapp.aiya.filter.SvSplitScreenFilter;
import com.aiyaapp.aiya.filter.SvThreeScreenFilter;
import com.aiyaapp.aiya.filter.SvTimeTunnelFilter;
import com.aiyaapp.aiya.filter.SvVirtualMirrorFilter;
import com.aiyaapp.aiyamediaeditor.bean.VedioEffectBean;
import com.aiyaapp.aiyamediaeditor.util.AiyaVedioHelper;
import com.aiyaapp.aiyamediaeditor.view.GlideCircleTransform;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 特效编辑
 */
public class EffectEditActivity extends AppCompatActivity implements View.OnClickListener {

    private String vedioPath;

    public static void newInstance(Activity activity, String path) {
        Intent intent = new Intent(activity, EffectEditActivity.class);
        intent.putExtra("PATH", path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_edit);
        initViews();
    }

    private void initViews() {
        initSurfaceView();
        initVedioEffectView();
        findViewById(R.id.return_view).setOnClickListener(this);
    }

    //===================================特效列表开始======================================================
    private RecyclerView effectView;
    private VedioEffectAdapter effectAdapter;

    public void initVedioEffectView() {
        effectView = (RecyclerView) findViewById(R.id.effect_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        effectView.setLayoutManager(linearLayoutManager);
        effectAdapter = new VedioEffectAdapter();
        effectView.setAdapter(effectAdapter);
        effectAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                effectAdapter.switchPosition(i);
                if (aiyaVedioHelper != null) {
                    aiyaVedioHelper.startEdit(effectAdapter.getItem(i).getmNowSvClazz());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_view://返回
                this.finish();
                break;
        }
    }


    public class VedioEffectAdapter extends BaseQuickAdapter<VedioEffectBean> {
        public VedioEffectAdapter() {
            super(R.layout.item_mirror, null);
            initEirrorList();
            setNewData(datas);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, VedioEffectBean vedioEffectBean) {
            ImageView imageView = baseViewHolder.getView(R.id.mirror_icon);
            imageView.setSelected(vedioEffectBean.isSelect());
            Glide.with(mContext).load(vedioEffectBean.getIcon()).transform(new GlideCircleTransform(mContext)).into(imageView);
            baseViewHolder.setText(R.id.mirror_name, vedioEffectBean.getName());
        }

        private List<VedioEffectBean> datas;

        private void initEirrorList() {
            if (datas == null) {
                datas = new ArrayList<>();
            }
            datas.add(new VedioEffectBean("无特效", R.mipmap.default_mirror_icon, LazyFilter.class));
            datas.add(new VedioEffectBean("灵魂出窍", R.mipmap.default_mirror_icon, SvSpiritFreedFilter.class));
            datas.add(new VedioEffectBean("抖动", R.mipmap.default_mirror_icon, SvShakeFilter.class));
            datas.add(new VedioEffectBean("黑魔法", R.mipmap.default_mirror_icon, SvBlackMagicFilter.class));
            datas.add(new VedioEffectBean("虚拟镜像", R.mipmap.default_mirror_icon, SvVirtualMirrorFilter.class));
            datas.add(new VedioEffectBean("荧光", R.mipmap.default_mirror_icon, SvFluorescenceFilter.class));
            datas.add(new VedioEffectBean("时光隧道", R.mipmap.default_mirror_icon, SvTimeTunnelFilter.class));
            datas.add(new VedioEffectBean("躁动", R.mipmap.default_mirror_icon, SvDysphoriaFilter.class));
            datas.add(new VedioEffectBean("终极变色", R.mipmap.default_mirror_icon, SvFinalZeligFilter.class));
            datas.add(new VedioEffectBean("动感分屏", R.mipmap.default_mirror_icon, SvSplitScreenFilter.class));
            datas.add(new VedioEffectBean("幻觉", R.mipmap.default_mirror_icon, SvHallucinationFilter.class));
            datas.add(new VedioEffectBean("70S", R.mipmap.default_mirror_icon, SvSeventysFilter.class));
            datas.add(new VedioEffectBean("炫酷转动", R.mipmap.default_mirror_icon, SvRollUpFilter.class));
            datas.add(new VedioEffectBean("四分屏", R.mipmap.default_mirror_icon, SvFourScreenFilter.class));
            datas.add(new VedioEffectBean("三分屏", R.mipmap.default_mirror_icon, SvThreeScreenFilter.class));
            datas.add(new VedioEffectBean("黑白闪烁", R.mipmap.default_mirror_icon, SvBlackWhiteTwinkleFilter.class));
            datas.add(new VedioEffectBean("转场动画", R.mipmap.default_mirror_icon, SvCutSceneFilter.class));
        }

        public void switchPosition(int position) {
            if (datas == null || datas.size() <= 0) return;
            for (int i = 0; i < datas.size(); i++) {
                if (i == position) {
                    datas.get(i).setSelect(true);
                } else {
                    datas.get(i).setSelect(false);
                }
                this.notifyDataSetChanged();
            }
        }
    }
    //===================================特效列表介绍======================================================
    //-------------------------------视频播放 开始---------------------------------------------
    private AiyaVedioHelper aiyaVedioHelper;
    private void initSurfaceView() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        vedioPath = getIntent().getStringExtra("PATH");
        if (TextUtils.isEmpty(vedioPath)) {
            this.finish();
            return;
        }
        aiyaVedioHelper = new AiyaVedioHelper(surfaceView, vedioPath);
        aiyaVedioHelper.startEdit(null);
    }
    //-------------------------------视频播放 结束---------------------------------------------

}
