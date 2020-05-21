/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyl.camera.video.utils;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.SurfaceHolder;

import com.xyl.camera.video.ICaptureListener;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;

/**
 * Camera related utilities.
 */
public class CameraHelper implements Camera.AutoFocusCallback {

    private static final String TAG = "CameraHelper";

    private static final int FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera mCamera;

    private ICaptureListener captureListener;

    private int viewWidth;//视图宽度
    private int viewHeight;//控件高度
    private int facing = FACING_BACK;//默认打开后置摄像头

    private MediaRecordHelper mRecordHelper;

    private CameraHelper() {
    }

    @IntDef({FACING_BACK, FACING_FRONT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Facing {

    }

    public static CameraHelper get() {
        return SingletonHolder.instance;
    }

    public void setCaptureListener(ICaptureListener captureListener) {
        this.captureListener = captureListener;
    }

    /**
     * 打开摄像头
     */
    public void open() {
        try {
            mCamera = getDefaultCamera(facing);
            if (mCamera != null) {
                mCamera.setDisplayOrientation(90);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭摄像头
     */
    public void close() {
        facing = FACING_BACK;
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        captureListener = null;
    }

    /**
     * 设置相机参数
     *
     * @param viewWidth
     * @param viewHeight
     */
    public void initSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }


    /**
     * 开启预览
     *
     * @param object
     */
    public void startPreview(Object object) {
        try {
            findBestPreviewSize(mCamera);
            if (object instanceof SurfaceTexture) {
                mCamera.setPreviewTexture((SurfaceTexture) object);
            } else if (object instanceof SurfaceHolder) {
                mCamera.setPreviewDisplay((SurfaceHolder) object);
            } else {
                throw new IllegalArgumentException("invalid display");
            }
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换摄像头
     *
     * @param object
     */
    public void switchCamera(Object object) {
        int newFacing = facing;
        if (facing == FACING_BACK) {
            newFacing = FACING_FRONT;
        } else if (facing == FACING_FRONT) {
            newFacing = FACING_BACK;
        }
        Log.i(TAG, "switchCamera: " + newFacing);
        if (newFacing != facing) {//todo 录制视频的时候，切换摄像头处理
            if (mCamera != null) {
                mCamera.release();
            }
            facing = newFacing;
            open();
            startPreview(object);
        }
    }

    /**
     * 设置分辨率
     *
     * @param camera
     */
    private void findBestPreviewSize(Camera camera) {
        float targetRatio = 1.0f * viewHeight / viewWidth;//因为是竖屏
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = findBestSize(previewSizes, targetRatio);
        if (bestSize != null) {
            Log.i(TAG, "最佳预览尺寸: " + Arrays.toString(new int[]{bestSize.width, bestSize.height}));
            try {
                parameters.setPreviewSize(bestSize.width, bestSize.height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        previewSizes = parameters.getSupportedPictureSizes();
        bestSize = findBestSize(previewSizes, targetRatio);
        if (bestSize != null) {
            Log.i(TAG, "最佳图片尺寸: " + Arrays.toString(new int[]{bestSize.width, bestSize.height}));
            try {
                parameters.setPictureSize(bestSize.width, bestSize.height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setFocusMode(FOCUS_MODE_CONTINUOUS_VIDEO);//使用camera的连续自动对焦，需要在第一次对焦成功以后，取消自动对焦
        camera.setParameters(parameters);
    }

    private Camera.Size findBestSize(List<Camera.Size> sizes, float targetRatio) {
        float minDiff = targetRatio;
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            if (size.width == viewWidth && size.height == viewHeight) {
                bestSize = size;
                break;
            }
            float supportedRatio = 1.0f * size.width / size.height;
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }
        return bestSize;
    }

    /**
     * 拍照
     *
     * @param filepath
     */
    public void takePicture(String filepath) {
        final DecodeFrameTask frameTask = new DecodeFrameTask(filepath, getFacingRotation());
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                frameTask.execute(data);
            }
        });
    }

    /**
     * 拍摄视频
     *
     * @param path     文件存放位置
     * @param duration 拍摄时长
     */
    public void takeVideo(String path, int duration) {
        mRecordHelper = new MediaRecordHelper(mCamera);
        if (captureListener != null) {
            mRecordHelper.setRecordListener(captureListener.generateRecorder());
        }
        mRecordHelper.setRotation(getFacingRotation());
        mRecordHelper.startRecord(path, duration);
    }

    /**
     * 录制或者保存图片的时候，数据角度纠正
     *
     * @return
     */
    private int getFacingRotation() {
        int rotation = 90;
        if (facing == FACING_FRONT) {
            rotation = 270;
        }
        return rotation;
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        if (mRecordHelper != null) {
            mRecordHelper.stopRecord();
            mRecordHelper = null;
        }
    }

    /**
     * 录制视频结束
     */
    public void recordFinish() {
        if (mRecordHelper != null) {
            mRecordHelper.recordFinish();
        }
    }


    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            mCamera.cancelAutoFocus();
        } else {
            mCamera.autoFocus(this);
        }
    }

    private static Camera getDefaultCamera(@Facing int facing) {
        // Find the total number of cameras available
        int mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the back-facing ("default") camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return Camera.open(i);

            }
        }
        return null;
    }

    private static class DecodeFrameTask extends AsyncTask<byte[], Void, File> {

        private float rotateDegrees;
        private String filepath;

        public DecodeFrameTask(String filepath, float rotateDegrees) {
            this.filepath = filepath;
            this.rotateDegrees = rotateDegrees;
        }

        @Override
        protected File doInBackground(byte[]... data) {
            return CameraUtils.savePhoto(data[0], rotateDegrees, filepath);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null && CameraHelper.get().captureListener != null) {
                CameraHelper.get().captureListener.onCaptured(file);
            }
        }
    }

    private static class SingletonHolder {
        private static final CameraHelper instance = new CameraHelper();
    }
}
