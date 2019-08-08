package com.xyl.camera.video.view;

import android.content.Context;
import android.os.Environment;
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
public class CaptureToolbar extends FrameLayout implements View.OnClickListener, IRoundProgressListener {

    private View cameraView;
    private View cancelView;
    private View okView;
    private RoundProgressButton progressButton;
    private View hintView;

    private CaptureView captureView;

    public CaptureToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CaptureToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    }

    public void bindCaptureView(CaptureView captureView) {
        this.captureView = captureView;
        captureView.setCaptureListener(new ICaptureListener() {
            @Override
            public void onCaptured(File file) {

            }

            @Override
            public IRecordListener generateRecorder() {
                return new SimpleRecordListener() {

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
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.capture_toolbar_camera) {
            if (captureView != null) {
                captureView.switchCamera();
            }
        } else if (i == R.id.capture_toolbar_cancel) {//取消，返回重新拍摄
            if (captureView != null) {
                captureView.onBackPressed();
            }
        } else if (i == R.id.capture_toolbar_ok) {//回传拍摄的文件
            if (captureView != null) {
                captureView.onCaptureResult();
            }
        }
    }

    @Override
    public void onSingleTap() {
        if (captureView != null) {
            hintView.setVisibility(GONE);
            String dir = Environment.getExternalStorageDirectory() + "/apidemo/photo/";
            String filename = System.currentTimeMillis() + ".jpg";
            String capturePath = dir.concat(filename);
            captureView.takePicture(capturePath);
        }
    }

    @Override
    public void onPressStart() {
        if (captureView != null) {
            hintView.setVisibility(GONE);
            cameraView.setAlpha(0);
            String dir = Environment.getExternalStorageDirectory() + "/apidemo/video/";
            String filename = System.currentTimeMillis() + ".mp4";
            String capturePath = dir.concat(filename);
            captureView.takeVideo(capturePath, 15 * 1000);
        }
    }

    @Override
    public void onPressEnd() {
        if (captureView != null) {
            captureView.stopRecord();
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
