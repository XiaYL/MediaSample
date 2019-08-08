package com.xyl.camera.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.xyl.camera.video.utils.CameraHelper;
import com.xyl.camera.video.ICaptureListener;

/**
 * author xiayanlei
 * date 2019/7/25
 */
public class CameraSurfaceView extends BaseSurfaceView {

    private CameraHelper mCameraHelper;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraHelper = CameraHelper.get();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        mCameraHelper.initSize(width, height);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        stopRecord();
        closeCamera();
    }

    public void setCaptureListener(ICaptureListener captureListener) {
        mCameraHelper.setCaptureListener(captureListener);
    }

    public void openCamera() {
        if (!hasSurface) {
            return;
        }
        mCameraHelper.open();
    }

    public void closeCamera() {
        mCameraHelper.close();
    }

    public void startPreview() {
        mCameraHelper.startPreview(mHolder);
    }

    public void switchCamera() {
        mCameraHelper.switchCamera(mHolder);
    }

    /**
     * 拍照
     *
     * @param path
     */
    public void takePicture(final String path) {
        mCameraHelper.takePicture(path);
    }

    /**
     * 拍摄视频
     *
     * @param path     文件存放位置
     * @param duration 拍摄时长
     */
    public void takeVideo(String path, int duration) {
        mCameraHelper.takeVideo(path, duration);
    }

    private void stopRecord() {
        mCameraHelper.stopRecord();
    }

    public void recordFinish() {
        mCameraHelper.recordFinish();
    }
}
