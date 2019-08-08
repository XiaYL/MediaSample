package com.xyl.camera.video;

import android.util.Log;

import java.io.File;

/**
 * author xiayanlei
 * date 2019/8/5
 */
public interface ICaptureListener {

    String TAG = "ICaptureListener";

    /**
     * 拍照成功
     *
     * @param file
     */
    void onCaptured(File file);

    IRecordListener generateRecorder();

    /**
     * 视频录制接口
     */
    interface IRecordListener {

        void onRecordStart();

        void onRecordProgress(int progress);

        void onRecordFinish(File file);

        void onRecordError();
    }

    interface IResultListener {
        void onResult(File file, boolean isVideo);
    }


    abstract class SimpleRecordListener implements IRecordListener {

        @Override
        public void onRecordStart() {
            Log.i(TAG, "onRecordStart: ");
        }

        @Override
        public void onRecordProgress(int progress) {
            Log.i(TAG, "onRecordProgress: " + progress);
        }

        @Override
        public void onRecordFinish(File file) {
            Log.i(TAG, "onRecordFinish: " + file.getPath());
        }

        @Override
        public void onRecordError() {
            Log.i(TAG, "onRecordError: ");
        }
    }
}
