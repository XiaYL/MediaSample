package com.xyl.camera.video.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.xyl.camera.R;
import com.xyl.camera.video.ICaptureListener;
import com.xyl.camera.video.IRoundProgressListener;

import java.io.File;

/**
 * author xiayanlei
 * date 2019/8/6
 */
public class CaptureToolbar extends FrameLayout implements View.OnClickListener,
        IRoundProgressListener {

    private View cameraView;
    private View cancelView;
    private View okView;
    private RoundProgressButton progressButton;
    private View hintView;
    private ICaptureView mCaptureView;
    private ICaptureListener.IRecordListener mRecordListener;

    public CaptureToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CaptureToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_capture_toolbar, this);
        cameraView = findViewById(R.id.capture_toolbar_camera);
        cancelView = findViewById(R.id.capture_toolbar_cancel);
        okView = findViewById(R.id.capture_toolbar_ok);
        progressButton = findViewById(R.id.capture_progress_button);
        hintView = findViewById(R.id.capture_hint_text);


        progressButton.setProgressListener(this);
        cameraView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        okView.setOnClickListener(this);
        findViewById(R.id.capture_more_opts).setOnClickListener(this);
    }

    public void attach(ICaptureView captureView) {
        mCaptureView = captureView;
    }

    protected ICaptureListener.IRecordListener getRecordListener() {
        if (mRecordListener == null) {
            mRecordListener = new ICaptureListener.SimpleRecordListener() {

                @Override
                public void onRecordProgress(int progress) {
                    super.onRecordProgress(progress);
                    progressButton.setProgress(progress);
                }

                @Override
                public void onRecordFinish(File file) {
                    super.onRecordFinish(file);
                    progressButton.endPress();
                }

                @Override
                public void onRecordError() {
                    super.onRecordError();
                    progressButton.endPress();
                    resetView();
                }
            };
        }
        return mRecordListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.capture_toolbar_camera) {
            if (mCaptureView != null) {
                mCaptureView.switchCamera();
            }
        } else if (i == R.id.capture_toolbar_cancel) {//取消，返回重新拍摄
            if (mCaptureView != null) {
                mCaptureView.onBackPressed();
            }
        } else if (i == R.id.capture_toolbar_ok) {//回传拍摄的文件
            if (mCaptureView != null) {
                mCaptureView.onCaptureResult();
            }
        } else if (i == R.id.capture_more_opts) {//暂时做一个添加滤镜的效果

        }
    }

    @Override
    public void onSingleTap() {
        if (mCaptureView != null) {
            hintView.setVisibility(GONE);
            mCaptureView.takePicture();
        }
    }

    @Override
    public void onPressStart() {
        if (mCaptureView != null) {
            hintView.setVisibility(GONE);
            cameraView.setAlpha(0);
            mCaptureView.takeVideo();
        }
    }

    @Override
    public void onPressEnd() {
        if (mCaptureView != null) {
            mCaptureView.stopRecord();
        }
    }

    /**
     * 预览的时候，操作按钮可见
     */
    public void capturePreview() {
        okView.setAlpha(1);
        cancelView.setAlpha(1);
        cameraView.setAlpha(0);
        progressButton.setAlpha(0);
    }

    /**
     * 重置操作按钮
     */
    public void resetView() {
        okView.setAlpha(0);
        cancelView.setAlpha(0);
        progressButton.setAlpha(1);
        cameraView.setAlpha(1);
    }
}
