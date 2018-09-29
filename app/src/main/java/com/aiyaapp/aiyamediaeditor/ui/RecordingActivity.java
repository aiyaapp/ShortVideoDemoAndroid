package com.aiyaapp.aiyamediaeditor.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.aiyaapp.aavt.av.CameraRecorder2;
import com.aiyaapp.aavt.bean.RecordBean;
import com.aiyaapp.aiyamediaeditor.util.FileUtil;
import com.aiyaapp.aiyamediaeditor.vedio.VideoMerge;
import com.aiyaapp.aiyamediaeditor_android.R;

import java.io.File;

/**
 * 录制
 */
public class RecordingActivity extends AppCompatActivity implements ViewController.OnViewControllerListener {


    private DefaultEffectFlinger mFlinger;

    public static void newInstance(Context context, RecordBean bean) {
        Intent intent = new Intent(context, RecordingActivity.class);
        intent.putExtra("RECORDBEAN", bean);
        context.startActivity(intent);
    }


    private RecordBean recordBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recording);
        recordBean = (RecordBean) getIntent().getSerializableExtra("RECORDBEAN");
        initViews();

    }


    private SurfaceView mSurfaceView;
    private CameraRecorder2 mCamera;
    private ViewController viewController;//布局控制类

    private void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mCamera = new CameraRecorder2();
        mCamera.setRecodConfig(recordBean);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mCamera.open();
                mCamera.setSurface(holder.getSurface());
                mCamera.setPreviewSize(width, height);
                mCamera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.close();
            }
        });

        mFlinger = new DefaultEffectFlinger(getApplicationContext());
        mCamera.setRecordAudio(true);
        mCamera.setRenderer(mFlinger);

        View mContainer = findViewById(R.id.conroller_layout);
        viewController = new ViewController(this, mContainer);
        viewController.setListener(this);

    }


    private String tempPathDir;
    private String vedioPath;

    private void initData() {
        File file = FileUtil.getExternalFilesDir(this);
        tempPathDir = file.getAbsolutePath() + "/record/" + System.currentTimeMillis() + "/";
        FileUtil.createDir(tempPathDir);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        if (viewController != null) {
            viewController.onStart();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (viewController != null) {
            viewController.onStop();
        }
    }

    /**
     * 切换相机
     */
    @Override
    public void switchCamera() {
        if (mCamera != null) {
            mCamera.swithCamera();
        }
    }

    /**
     * 设置特效
     *
     * @param path
     */
    @Override
    public void setEffect(String path) {
        if (mFlinger != null) {
            mFlinger.setEffect(path);
        }
    }


    /**
     * 是否打开美颜
     *
     * @param openBeauty
     */
    @Override
    public void setOpenBeauty(boolean openBeauty) {
        if (mFlinger != null) {
            mFlinger.setIsOpenBeauty(openBeauty);
        }
    }


    private int i = 0;

    /**
     * 录制或暂停
     *
     * @param start
     */
    @Override
    public void startOrPauseRecord(boolean start, boolean finish) {
        if (start) {//开始录制
            i++;
            vedioPath = tempPathDir + i + ".mp4";
            mCamera.setOutputPath(vedioPath);
            mCamera.startRecord();
        } else {//停止录制
            mCamera.close();
            if (finish) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recordFinish();
                    }
                }, 1000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.open();
                        mCamera.startPreview();
                    }
                }, 100);
            }

        }
    }

    /**
     * 录制完成开始合成视频
     */
    private VideoMerge videoMerge;

    @Override
    public void recordFinish() {
        videoMerge = new VideoMerge(tempPathDir);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = videoMerge.merge(); //视频合成完成
                if (isOk) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VedioEditActivity.newInstance(RecordingActivity.this, tempPathDir + "record.mp4");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 视频回删
     */
    @Override
    public void deleteLastFile() {
        vedioPath = tempPathDir + i + ".mp4";
        i--;
        FileUtil.deleteFile(vedioPath);
    }


    /**
     * 设置录制速度
     *
     * @param speed
     */
    @Override
    public void setSpeed(float speed) {
        mCamera.setmVideoSpeed(speed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlinger.release();
    }
}
