package com.aiyaapp.aiyamediaeditor.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aiyaapp.aiyamediaeditor.bean.MusicBean;
import com.aiyaapp.aiyamediaeditor.ui.adapter.MusicAdapter;
import com.aiyaapp.aiyamediaeditor.util.MediaUtils;
import com.aiyaapp.aiyamediaeditor_android.R;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择合成音频
 */

public class SelectMusioActivity extends AppCompatActivity implements View.OnClickListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_MUSIC_CODE = 2000;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;

    public static void newInstance(Activity activity) {
        activity.startActivityForResult(new Intent(activity, SelectMusioActivity.class), REQUEST_MUSIC_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_musio);
        initViews();
        initData();
    }


    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.music_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter();
        recyclerView.setAdapter(musicAdapter);

        musicAdapter.setOnRecyclerViewItemClickListener(this);

        findViewById(R.id.return_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_view:
                this.finish();
                break;
        }
    }


    /**
     * item 点击事件
     *
     * @param view
     * @param i
     */
    @Override
    public void onItemClick(View view, int i) {
        if (musicAdapter != null) {
            musicAdapter.switchPosition(i);
            Intent intent = new Intent();
            intent.putExtra("MUSICURL", musicAdapter.getItem(i).fileUrl);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    /**
     * 初始数据
     */
    private void initData() {
        new GetMusictTask(this).execute();
    }

    //===========================获取音乐信息 开始======================================================================================
    private static final String[] SUPPORT_DECODE_AUDIOFORMAT = {"audio/mpeg", "audio/x-ms-wma", "audio/mp4a-latm", "audio/x-wav"};
    private int mPage = 1;
    private static final int PAGE_SIZE = 20;


    public class GetMusictTask extends AsyncTask<Void, Void, List<MusicBean>> {
        Context context;

        GetMusictTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<MusicBean> doInBackground(Void... params) {
            return getLocalAudioes(context, mPage, PAGE_SIZE);
        }

        @Override
        protected void onPostExecute(List<MusicBean> result) {
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                if (musicAdapter != null) {
                    musicAdapter.setDatas(result);
                }
            }
        }

        private ArrayList<MusicBean> getLocalAudioes(Context context, int page, int pageSize) {

            ArrayList<MusicBean> audios = null;

            StringBuilder selectionBuilder = new StringBuilder();
            int size = SUPPORT_DECODE_AUDIOFORMAT.length;
            for (int i = 0; i != size; ++i) {
                selectionBuilder.append("mime_type=? or ");
            }
            int sbLen = selectionBuilder.length();
            selectionBuilder.delete(sbLen - 3, sbLen);

            final String selection = selectionBuilder.toString();
            final String orderBy = String.format("%s LIMIT %s , %s ", MediaStore.Audio.Media.DEFAULT_SORT_ORDER, (page - 1) * pageSize, pageSize);

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.YEAR,
                            MediaStore.Audio.Media.MIME_TYPE,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.DATA},
                    selection,
                    SUPPORT_DECODE_AUDIOFORMAT, orderBy);

            audios = new ArrayList<MusicBean>();

            if (cursor.moveToFirst()) {

                MusicBean audioEntry = null;

                String fileUrl = null;
                boolean isMatchAudioFormat = false;
                do {
                    fileUrl = cursor.getString(9);
                    isMatchAudioFormat = MediaUtils.isMatchAudioFormat(fileUrl, 44100, 2);
                    if (!isMatchAudioFormat) {
                        continue;
                    }

                    audioEntry = new MusicBean();
                    audioEntry.id = cursor.getLong(0);
                    // 文件名
                    audioEntry.fileName = cursor.getString(1);
                    // 歌曲名
                    audioEntry.title = cursor.getString(2);
                    // 时长
                    audioEntry.duration = cursor.getInt(3);
                    // 歌手名
                    audioEntry.artist = cursor.getString(4);
                    // 专辑名
                    audioEntry.album = cursor.getString(5);
                    // 年代
                    audioEntry.year = cursor.getString(6);
                    // 歌曲格式
                    audioEntry.mime = cursor.getString(7).trim();
                    // 文件大小
                    audioEntry.size = cursor.getString(8);
                    // 文件路径
                    audioEntry.fileUrl = fileUrl;
                    audios.add(audioEntry);
                } while (cursor.moveToNext());

                cursor.close();

            }
            return audios;
        }
    }

//===========================获取音乐信息 结束======================================================================================
}
