package com.aiyaapp.aiyamediaeditor.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiyaapp.aavt.bean.RecordBean;
import com.aiyaapp.aiya.AiyaEffects;
import com.aiyaapp.aiyamediaeditor.util.FileUtil;
import com.aiyaapp.aiyamediaeditor.util.GetPathFromUri4kitkat;
import com.aiyaapp.aiyamediaeditor.util.PermissionUtils;
import com.aiyaapp.aiyamediaeditor.vedio.VideoMerge;
import com.aiyaapp.aiyamediaeditor_android.R;

import java.io.File;

/**
 * 录制设置
 */
public class RecordSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoMerge videoMerge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPremission();
    }

    /**
     * 初始化权限
     */
    private void initPremission() {
        PermissionUtils.askPermission(this, new String[]{
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO
        }, 10, start);
    }

    /**
     * 权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, start, new Runnable() {
            @Override
            public void run() {
                finish();
                Toast.makeText(RecordSettingActivity.this, "必要的权限未被允许", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Runnable start = new Runnable() {
        @Override
        public void run() {
            AiyaEffects.init(getApplicationContext(), "477de67d19ba39fb656a4806c803b552");
            initViews();

        }
    };


    private TextView rate_480, rate_720, rate_1080;//视频分辨率
    private TextView frame_15, frame_24, frame_30;//视频帧率
    private TextView v_ratio_2048, v_ratio_4069, v_ratio_8192;//视频码率
    private TextView a_ratio_64, a_ratio_128, a_ratio_256;//音频码率
    private TextView music_no, music_1, music_2;//选取音乐

    private void initViews() {
        setContentView(R.layout.activity_recordsetting);

        rate_480 = (TextView) findViewById(R.id.rate_480);
        rate_720 = (TextView) findViewById(R.id.rate_720);
        rate_1080 = (TextView) findViewById(R.id.rate_1080);
        rate_480.setOnClickListener(this);
        rate_720.setOnClickListener(this);
        rate_1080.setOnClickListener(this);
        switchRate(1);

        frame_15 = (TextView) findViewById(R.id.frame_15);
        frame_24 = (TextView) findViewById(R.id.frame_24);
        frame_30 = (TextView) findViewById(R.id.frame_30);
        frame_15.setOnClickListener(this);
        frame_24.setOnClickListener(this);
        frame_30.setOnClickListener(this);
        switchFrame(1);

        v_ratio_2048 = (TextView) findViewById(R.id.v_ratio_2048);
        v_ratio_4069 = (TextView) findViewById(R.id.v_ratio_4069);
        v_ratio_8192 = (TextView) findViewById(R.id.v_ratio_8192);
        v_ratio_2048.setOnClickListener(this);
        v_ratio_4069.setOnClickListener(this);
        v_ratio_8192.setOnClickListener(this);
        switchVRatio(1);

        a_ratio_64 = (TextView) findViewById(R.id.a_ratio_64);
        a_ratio_128 = (TextView) findViewById(R.id.a_ratio_128);
        a_ratio_256 = (TextView) findViewById(R.id.a_ratio_256);
        a_ratio_64.setOnClickListener(this);
        a_ratio_128.setOnClickListener(this);
        a_ratio_256.setOnClickListener(this);
        switchARatio(1);

        music_no = (TextView) findViewById(R.id.music_no);
        music_1 = (TextView) findViewById(R.id.music_1);
        music_2 = (TextView) findViewById(R.id.music_2);
        music_no.setOnClickListener(this);
        music_1.setOnClickListener(this);
        music_2.setOnClickListener(this);
        switchMusic(0);

        findViewById(R.id.aiya_import).setOnClickListener(this);
        findViewById(R.id.aiya_shooting).setOnClickListener(this);
    }

    private int currentRate;
    private int currentFrame;
    private int currentVRatio;
    private int currentARatio;
    private String currentMusic;

    private void switchRate(int type) {
        rate_480.setSelected(false);
        rate_720.setSelected(false);
        rate_1080.setSelected(false);
        currentRate = 720;
        switch (type) {
            case 0:
                currentRate = 480;
                rate_480.setSelected(true);
                break;
            case 1:
                currentRate = 720;
                rate_720.setSelected(true);
                break;
            case 2:
                currentRate = 1080;
                rate_1080.setSelected(true);
                break;
        }
    }

    private void switchFrame(int type) {
        frame_15.setSelected(false);
        frame_24.setSelected(false);
        frame_30.setSelected(false);
        currentFrame = 24;
        switch (type) {
            case 0:
                frame_15.setSelected(true);
                currentFrame = 15;
                break;
            case 1:
                frame_24.setSelected(true);
                currentFrame = 24;
                break;
            case 2:
                currentFrame = 30;
                frame_30.setSelected(true);
                break;
        }
    }

    private void switchVRatio(int type) {
        v_ratio_2048.setSelected(false);
        v_ratio_4069.setSelected(false);
        v_ratio_8192.setSelected(false);
        currentVRatio = 4069;
        switch (type) {
            case 0:
                v_ratio_2048.setSelected(true);
                currentVRatio = 2048;
                break;
            case 1:
                v_ratio_4069.setSelected(true);
                currentVRatio = 4069;
                break;
            case 2:
                currentVRatio = 8192;
                v_ratio_8192.setSelected(true);
                break;
        }
    }

    private void switchARatio(int type) {
        currentARatio = 128;
        a_ratio_64.setSelected(false);
        a_ratio_128.setSelected(false);
        a_ratio_256.setSelected(false);
        switch (type) {
            case 0:
                a_ratio_64.setSelected(true);
                currentARatio = 64;
                break;
            case 1:
                a_ratio_128.setSelected(true);
                currentARatio = 128;
                break;
            case 2:
                a_ratio_256.setSelected(true);
                currentARatio = 256;
                break;
        }
    }

    private void switchMusic(int type) {
        currentMusic = "";
        music_no.setSelected(false);
        music_1.setSelected(false);
        music_2.setSelected(false);
        switch (type) {
            case 0:
                music_no.setSelected(true);
                currentMusic = "";
                break;
            case 1:
                music_1.setSelected(true);
                currentMusic = "";
                break;
            case 2:
                music_2.setSelected(true);
                currentMusic = "";
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aiya_import://导入视频
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/mp4"); //选择视频 （mp4 3gp 是android支持的视频格式）
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.aiya_shooting://视频录制
                RecordBean bean = new RecordBean(currentRate, currentFrame, currentVRatio, currentARatio);
                RecordingActivity.newInstance(this, bean);
                break;
            case R.id.rate_480: //视频分辨率
                switchRate(0);
                break;
            case R.id.rate_720:
                switchRate(1);
                break;
            case R.id.rate_1080:
                switchRate(2);
                break;
            case R.id.frame_15://视频帧率
                switchFrame(0);
                break;
            case R.id.frame_24:
                switchFrame(1);
                break;
            case R.id.frame_30:
                switchFrame(2);
                break;
            case R.id.v_ratio_2048://视频码率
                switchVRatio(0);
                break;
            case R.id.v_ratio_4069:
                switchVRatio(1);
                break;
            case R.id.v_ratio_8192:
                switchVRatio(2);
                break;
            case R.id.a_ratio_64://音频码率
                switchARatio(0);
                break;
            case R.id.a_ratio_128:
                switchARatio(1);
                break;
            case R.id.a_ratio_256:
                switchARatio(2);
                break;
            case R.id.music_no://选取音乐
                switchMusic(0);
                break;
            case R.id.music_1:
                switchMusic(1);
                break;
            case R.id.music_2:
                switchMusic(2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path = getRealFilePath(data.getData());
            if (path != null) {//视频路径
                initData();
                vedioPath = tempPathDir + "1.mp4";
                boolean b = FileUtil.copyFile(path, vedioPath);
                if (b) {
                    recordFinish();
                }
            }
        }
    }


    private String tempPathDir;
    private String vedioPath;

    private void initData() {
        File file = FileUtil.getExternalFilesDir(this);
        tempPathDir = file.getAbsolutePath() + "/record/" + System.currentTimeMillis() + "/";
        FileUtil.createDir(tempPathDir);
    }



    /**
     * 视频合成
     */
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
                            VedioEditActivity.newInstance(RecordSettingActivity.this, tempPathDir + "record.mp4");
                        }
                    });
                }
            }
        }).start();
    }



    public String getRealFilePath(final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            data = GetPathFromUri4kitkat.getPath(getApplicationContext(), uri);
        }
        return data;
    }

}
