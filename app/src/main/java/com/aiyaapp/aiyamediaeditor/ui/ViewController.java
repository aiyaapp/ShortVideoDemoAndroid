package com.aiyaapp.aiyamediaeditor.ui;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiyaapp.aiyamediaeditor.bean.EffectBean;
import com.aiyaapp.aiyamediaeditor.ui.adapter.EffectAdapter;
import com.aiyaapp.aiyamediaeditor.view.RecordProgressView;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.chad.library.adapter.base.BaseQuickAdapter;


public class ViewController implements View.OnClickListener {

    private Activity act;
    private RecyclerView effectRecyclerView, mirrorRecyclerView;
    private EffectAdapter effectAdapter;
    private MirrorAdapter mirrorAdapter;
    //断点拍摄进度控制
    private RecordProgressController mRecordProgressCtl;
    private RecordProgressView mRecordProgressView;

    public ViewController(final Activity act, View rootView) {
        this.act = act;
        intiViews(rootView);
    }

    private TextView record_speed_0_5, record_speed_1, record_speed_1_5, record_speed_2, record_speed_2_5;
    private View buttom_layout, effect_layout, beauty_layout, mirror_layout, head_layout, effect_face, speed_layout;
    private TextView beauty_view;//美颜开关
    private ImageView record_view, delete_view;

    private void intiViews(View rootView) {
        if (rootView == null) return;
        buttom_layout = rootView.findViewById(R.id.buttom_layout1);
        effect_layout = rootView.findViewById(R.id.effect_layout);
        mirror_layout = rootView.findViewById(R.id.mirrorList_layout);
        head_layout = rootView.findViewById(R.id.head_layout);

        beauty_layout = rootView.findViewById(R.id.beauty_layout);
        beauty_view = rootView.findViewById(R.id.beauty_view);

        record_view = rootView.findViewById(R.id.record_view);
        record_view.setOnClickListener(this);

        delete_view = rootView.findViewById(R.id.delete_view);
        effect_face = rootView.findViewById(R.id.effect_face);
        speed_layout = rootView.findViewById(R.id.speed_layout);

        //断点拍摄UI初始化
        mRecordProgressView = (RecordProgressView) rootView.findViewById(R.id.record_progress);


        record_speed_0_5 = rootView.findViewById(R.id.record_speed_0_5);
        record_speed_1 = rootView.findViewById(R.id.record_speed_1);
        record_speed_1_5 = rootView.findViewById(R.id.record_speed_1_5);
        record_speed_2 = rootView.findViewById(R.id.record_speed_2);
        record_speed_2_5 = rootView.findViewById(R.id.record_speed_2_5);
        record_speed_0_5.setOnClickListener(this);
        record_speed_1.setOnClickListener(this);
        record_speed_1_5.setOnClickListener(this);
        record_speed_2.setOnClickListener(this);
        record_speed_2_5.setOnClickListener(this);
        switchSpeedView(2);


        effectRecyclerView = rootView.findViewById(R.id.mEffectList);
        initEffectView();

        mirrorRecyclerView = rootView.findViewById(R.id.mirror_recyclerview);
        initMirrorView();


        rootView.findViewById(R.id.return_view).setOnClickListener(this);
        rootView.findViewById(R.id.switch_camera).setOnClickListener(this);
        rootView.findViewById(R.id.next_view).setOnClickListener(this);
        rootView.findViewById(R.id.mirror_layout).setOnClickListener(this);

        delete_view.setOnClickListener(this);
        effect_face.setOnClickListener(this);
        rootView.setOnClickListener(this);
        beauty_layout.setOnClickListener(this);
    }

    /**
     * 停止
     */
    public void onStop() {
        mRecordProgressCtl.stop();
        mRecordProgressCtl.setRecordingLengthChangedListener(null);
        mRecordProgressCtl.release();
    }


    public void onStart() {
        // 需要放在这里，因为在Release是在onStop中调用的，会把内部的资源释放掉。
        mRecordProgressCtl = new RecordProgressController(mRecordProgressView, null);
        mRecordProgressCtl.setRecordingLengthChangedListener(mRecordLengthChangedListener);
        mRecordProgressCtl.start();
    }
    private float mSpeed = 1.0f;
    private void switchSpeedView(int type) {
        record_speed_0_5.setSelected(false);
        record_speed_1.setSelected(false);
        record_speed_1_5.setSelected(false);
        record_speed_2.setSelected(false);
        record_speed_2_5.setSelected(false);
        switch (type) {
            case 0:
                record_speed_0_5.setSelected(true);
                mSpeed = 1.75f;
                break;
            case 1:
                record_speed_1.setSelected(true);
                mSpeed = 1.5f;
                break;
            case 2:
                record_speed_1_5.setSelected(true);
                mSpeed = 1.0f;
                break;
            case 3:
                mSpeed = 0.5f;
                record_speed_2.setSelected(true);
                break;
            case 4:
                record_speed_2_5.setSelected(true);
                mSpeed = 0.25f;
                break;
        }
        if (mListener != null) {
            mListener.setSpeed(mSpeed);
        }
    }

    private boolean isBeauty = false;
    private boolean isRecord = false;


    /**
     * 开始录制
     */
    private void startOrPauseRecord(boolean finish) {
        isRecord = !isRecord;
        record_view.setSelected(isRecord);
        if (mListener != null) {
            mListener.startOrPauseRecord(isRecord, finish);
            if (isRecord) {
                head_layout.setVisibility(View.GONE);
                speed_layout.setVisibility(View.GONE);
                effect_face.setVisibility(View.GONE);
                delete_view.setVisibility(View.GONE);
                mRecordProgressCtl.startRecording();
            } else {
                speed_layout.setVisibility(View.VISIBLE);
                delete_view.setVisibility(View.VISIBLE);
                effect_face.setVisibility(View.VISIBLE);
                head_layout.setVisibility(View.VISIBLE);
                mRecordProgressCtl.stopRecording();
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_view://删除录制
                mRecordProgressCtl.rollback();
                if (mListener != null) {
                    mListener.deleteLastFile();
                }
                break;
            case R.id.record_view://开始录制
                startOrPauseRecord(false);
                break;
            case R.id.beauty_layout://是否美颜
                if (isBeauty) {
                    beauty_view.setText("美颜关");
                    beauty_layout.setSelected(false);
                } else {
                    beauty_view.setText("美颜开");
                    beauty_layout.setSelected(true);
                }
                isBeauty = !isBeauty;
                if (mListener != null) {
                    mListener.setOpenBeauty(isBeauty);
                }
                break;
            case R.id.conroller_layout://还原
                effect_layout.setVisibility(View.GONE);
                mirror_layout.setVisibility(View.GONE);
                head_layout.setVisibility(View.VISIBLE);
                buttom_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.effect_face://打开特效
                head_layout.setVisibility(View.GONE);
                mirror_layout.setVisibility(View.GONE);
                buttom_layout.setVisibility(View.GONE);
                effect_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.mirror_layout://滤镜
                if (mirror_layout.getVisibility() == View.VISIBLE) {
                    effect_layout.setVisibility(View.GONE);
                    mirror_layout.setVisibility(View.GONE);
                    buttom_layout.setVisibility(View.VISIBLE);
                } else {
                    effect_layout.setVisibility(View.GONE);
                    buttom_layout.setVisibility(View.GONE);
                    mirror_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.return_view://返回
                act.finish();
                break;
            case R.id.switch_camera://切换相机
                if (mListener != null) {
                    mListener.switchCamera();
                }
                break;
            case R.id.next_view://下一步
                if (mListener != null) {
                    mListener.recordFinish();
                }
                break;
            case R.id.record_speed_0_5:
                switchSpeedView(0);
                break;
            case R.id.record_speed_1:
                switchSpeedView(1);
                break;
            case R.id.record_speed_1_5:
                switchSpeedView(2);
                break;
            case R.id.record_speed_2:
                switchSpeedView(3);
                break;
            case R.id.record_speed_2_5:
                switchSpeedView(4);
                break;
        }
    }


    //-----------------------------页面监听回调-------------------------------------------
    public void setListener(OnViewControllerListener mListener) {
        this.mListener = mListener;
    }


    private OnViewControllerListener mListener;


    public interface OnViewControllerListener {
        void switchCamera();//切换相机

        void setEffect(String path);

        void setOpenBeauty(boolean openBeauty);

        void startOrPauseRecord(boolean start, boolean finish);//开始或暂停录制

        void recordFinish();//录制完成

        void deleteLastFile();//视频回删

        void setSpeed(float speed);
    }


    //-----------------------------人脸特效-------------------------------------------
    private void initEffectView() {
        if (effectRecyclerView != null) {
            effectRecyclerView.setLayoutManager(new GridLayoutManager(act.getApplicationContext(), 4));
            effectAdapter = new EffectAdapter(act);
            effectRecyclerView.setAdapter(effectAdapter);
            effectAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    effectAdapter.switchPosition(i);
                    EffectBean bean = effectAdapter.getItem(i);
                    if (bean != null) {
                        String path = "assets/modelsticker/" + bean.path;
                        if (i == 0) {
                            path = null;
                        }
                        if (mListener != null) {
                            mListener.setEffect(path);
                        }
                    }
                }
            });
        }
    }


    //-----------------------------滤镜-------------------------------------------
    private void initMirrorView() {
        if (mirrorRecyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(act);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mirrorRecyclerView.setLayoutManager(linearLayoutManager);
            mirrorAdapter = new MirrorAdapter();
            mirrorRecyclerView.setAdapter(mirrorAdapter);
            mirrorAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    mirrorAdapter.switchPosition(i);
                }
            });
        }
    }

    //-----------------------------录制监听-------------------------------------------
    private RecordProgressController.RecordingLengthChangedListener mRecordLengthChangedListener =
            new RecordProgressController.RecordingLengthChangedListener() {
                @Override
                public void passMinPoint(boolean pass) {
                    if (pass) {
                        //超过最短时长显示下一步按钮，否则不能进入编辑，最短时长可自行设定，Demo中当前设定为5s
                    }
                }

                @Override
                public void passMaxPoint() {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //到达最大拍摄时长时，需要主动停止录制
                            startOrPauseRecord(true);


                        }
                    });
                }
            };

}
