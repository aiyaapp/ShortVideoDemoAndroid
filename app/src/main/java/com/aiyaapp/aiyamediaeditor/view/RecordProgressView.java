package com.aiyaapp.aiyamediaeditor.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.aiyaapp.aiyamediaeditor.ui.RecordProgressController;
import com.aiyaapp.aiyamediaeditor.bean.RecordClipModel;
import com.aiyaapp.aiyamediaeditor.config.RecordConfig;
import com.aiyaapp.aiyamediaeditor_android.R;

import java.util.LinkedList;


/**
 * 进度条布局
 */

public class RecordProgressView extends View implements RecordProgressController.RecordingStateChanged {
    private static final String TAG = "RecordProgressView";

    private int paddingTop = 2;
    private int paddingBottom = 2;
    private Paint mPaint;
    private Paint mMinPaint;
    private Paint mPausedPaint;
    private int mMinPoint;
    private Handler mHandler;
    private boolean mFlag;
    private Bitmap mCursorBitmap;

    private LinkedList<RecordClipModel> mProgressClipList;
    public int mTotalWidth;
    private Paint mPendingPaint;
    public int mScreenWidth;

    private boolean mIsRecording;

    public RecordProgressView(Context context) {
        super(context);
        instantiate(context);
    }

    public RecordProgressView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        instantiate(context);
    }

    public RecordProgressView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        instantiate(context);
    }

    private void instantiate(Context context) {
        mPaint = new Paint();
        mMinPaint = new Paint();
        mPausedPaint = new Paint();
        mPendingPaint = new Paint();
        mTotalWidth = 0;
        mProgressClipList = null;
        mHandler = new Handler();
        mFlag = false;
        Resources res = context.getResources();
        mCursorBitmap = BitmapFactory.decodeResource(res,
                R.mipmap.record_progressbar_front);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.record_progress_blue));
        mPausedPaint.setStyle(Paint.Style.FILL);
        mPausedPaint.setColor(getResources().getColor(
                R.color.record_progress_pause));
        mMinPaint.setStyle(Paint.Style.FILL);
        mMinPaint.setColor(getResources().getColor(
                R.color.record_progress_pause));
        mPendingPaint.setStyle(Paint.Style.FILL);
        mPendingPaint.setColor(getResources().getColor(
                R.color.record_progress_red));
        mScreenWidth = getScreenWidthPixels(getContext());
        mMinPoint = mScreenWidth
                * RecordConfig.MIN_DURATION
                / RecordConfig.MAX_DURATION;
        mHandler.postDelayed(mCursorRunnable, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean pendingDelete = false;
        if (mProgressClipList != null && !mProgressClipList.isEmpty()) {
            int totalWidth = 0;
            for (RecordClipModel clip : mProgressClipList) {
                long newWidth = totalWidth + (clip.timeInterval * mScreenWidth)
                        / RecordConfig.MAX_DURATION;
                switch (clip.state) {
                    case 0: // recording
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPaint);
                        break;
                    case 1: // recorded
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPaint);
                        canvas.drawRect(newWidth - 12, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPausedPaint);
                        break;
                    case 2: // pending fro delete
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPendingPaint);
                        pendingDelete = true;
                        break;
                    default:
                        break;
                }
                totalWidth = (int) newWidth;
            }
            mTotalWidth = totalWidth;
        } else {
            mTotalWidth = 0;
        }

        if (mTotalWidth < mMinPoint) {// TODO: 2018/1/26 0026  设置最小时长标志
//                 canvas.drawRect(mMinPoint, paddingTop, mMinPoint + 3,
//                    getMeasuredHeight() - paddingBottom, mMinPaint);
        }

        if ((mFlag && !pendingDelete) || mIsRecording) {
            canvas.drawBitmap(mCursorBitmap, null, new Rect(mTotalWidth - 20,
                    paddingTop, mTotalWidth + 12, getMeasuredHeight() - paddingBottom), null);
        }


    }


    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mProgressClipList.clear();
    }

    public void setProgressClipList(
            LinkedList<RecordClipModel> clips) {
        mProgressClipList = clips;
    }

    public boolean isPassMinPointQuick() {
        if (mProgressClipList != null && !mProgressClipList.isEmpty()) {
            int totalWidth = 0;
            for (RecordClipModel clip : mProgressClipList) {
                long newWidth = totalWidth + (clip.timeInterval * mScreenWidth)
                        / RecordConfig.MAX_DURATION;
                totalWidth = (int) newWidth;
            }
            if (totalWidth >= mMinPoint) {
                return true;
            }
        }
        return false;
    }

    public boolean isPassMinPoint() {
        if (mTotalWidth >= mMinPoint) {
            return true;
        }
        return false;
    }

    public boolean isPassMaxPoint() {
        if (mTotalWidth >= mScreenWidth) {
            return true;
        }
        return false;
    }

    private Runnable mCursorRunnable = new Runnable() {
        @Override
        public void run() {
            mFlag = !mFlag;
            mHandler.postDelayed(mCursorRunnable, 500);
            invalidate();
        }
    };

    private int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void recordingStart(long startTime) {
        mIsRecording = true;
    }

    @Override
    public void recordingStop() {
        mIsRecording = false;
    }
}
