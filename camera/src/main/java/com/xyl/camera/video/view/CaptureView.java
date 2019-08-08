package com.xyl.camera.video.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.xyl.camera.video.ICaptureListener;
import com.xyl.camera.video.IMediaListener;

import java.io.File;

/**
 * author xiayanlei
 * date 2019/8/6
 */
public class CaptureView extends FrameLayout implements ICaptureListener, ICaptureListener.IRecordListener {

    private FrameLayout surfaceContainer;//拍摄视图容器
    private CameraSurfaceView cameraSurfaceView;//摄像头显示画面
    private PanelSurfaceView panelSurfaceView;//拍照完成以后，自动显示图片
    private MediaSurfaceView mediaSurfaceView;//视频录制完成以后，自动播放视频
    private ICaptureListener captureListener;
    private IRecordListener recordListener;//视频录制监听
    private CaptureToolbar toolbar;//操作按钮栏
    private IResultListener resultListener;//拍照处理结果
    private File outFile;//拍摄文件
    private boolean isVideo;//文件是否是视频

    public CaptureView(@NonNull Context context) {
        this(context, null);
    }

    public CaptureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        surfaceContainer = new FrameLayout(context);
        addView(surfaceContainer);

        cameraSurfaceView = new CameraSurfaceView(context);//添加相机布局
        surfaceContainer.addView(cameraSurfaceView);
        cameraSurfaceView.setCaptureListener(this);

        toolbar = new CaptureToolbar(context);
        addView(toolbar);
        toolbar.bindCaptureView(this);
    }

    public void setCaptureListener(ICaptureListener captureListener) {
        this.captureListener = captureListener;
        if (captureListener != null) {
            recordListener = captureListener.generateRecorder();
        }
    }

    public void setResultListener(IResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void switchCamera() {
        cameraSurfaceView.switchCamera();
    }

    /**
     * 拍照
     *
     * @param path
     */
    public void takePicture(final String path) {
        cameraSurfaceView.takePicture(path);
    }

    /**
     * 拍摄视频
     *
     * @param path     文件存放位置
     * @param duration 拍摄时长
     */
    public void takeVideo(String path, int duration) {
        cameraSurfaceView.takeVideo(path, duration);
    }

    public void stopRecord() {
        cameraSurfaceView.recordFinish();
    }

    @Override
    public void onCaptured(File file) {
        isVideo = false;
        outFile = file;
        if (captureListener != null) {
            captureListener.onCaptured(file);
        }
        addPanelSurface(file);
    }

    @Override
    public IRecordListener generateRecorder() {
        return this;
    }


    @Override
    public void onRecordStart() {
        if (recordListener != null) {
            recordListener.onRecordStart();
        }
    }

    @Override
    public void onRecordProgress(int progress) {
        if (recordListener != null) {
            recordListener.onRecordProgress(progress);
        }
    }

    @Override
    public void onRecordFinish(File file) {
        isVideo = true;
        outFile = file;
        if (recordListener != null) {
            recordListener.onRecordFinish(file);
        }
        addMediaSurface(file);//录制完成以后，直接播放
    }

    @Override
    public void onRecordError() {
        if (recordListener != null) {
            recordListener.onRecordError();
        }
    }

    public boolean onBackPressed() {
        Log.i(TAG, "onBackPressed: ");
        if (panelSurfaceView != null) {
            removePanelSurface();
            cameraSurfaceView.startPreview();
            return true;
        }
        if (mediaSurfaceView != null) {//正在播放视频，删除视频并退回到视频采集页面
            removeMediaSurface();
            return true;
        }
        return false;
    }

    private void addPanelSurface(File file) {
        toolbar.capturePreview();
        panelSurfaceView = new PanelSurfaceView(getContext());
        surfaceContainer.addView(panelSurfaceView);
        panelSurfaceView.drawBitmap(file.getPath());
    }

    private void removePanelSurface() {
        if (panelSurfaceView != null) {
            toolbar.resetView();
            surfaceContainer.removeView(panelSurfaceView);
            panelSurfaceView = null;
            deleteFile();
        }
    }

    private void addMediaSurface(File file) {
        toolbar.capturePreview();
        mediaSurfaceView = new MediaSurfaceView(getContext());
        mediaSurfaceView.setMediaListener(new IMediaListener() {
            @Override
            public void onComplete(MediaPlayer mp) {
                mp.start();
            }

            @Override
            public void onPrepared(int duration) {
                Log.i(TAG, "onPrepared: " + duration);
            }
        });
        surfaceContainer.addView(mediaSurfaceView);
        mediaSurfaceView.play(file.getPath());
    }

    private void removeMediaSurface() {
        if (mediaSurfaceView != null) {
            toolbar.resetView();
            surfaceContainer.removeView(mediaSurfaceView);
            mediaSurfaceView = null;
            deleteFile();
        }
    }

    private void deleteFile() {
        if (outFile != null) {
            outFile.delete();
            outFile = null;
        }
    }

    public void onCaptureResult() {
        if (outFile != null && resultListener != null) {
            resultListener.onResult(outFile, isVideo);
            outFile = null;
        }
    }
}
