package com.xyl.camera.audio;

import android.media.AudioRecord;
import android.util.Log;

import com.xyl.camera.video.utils.CameraUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class AudioRecordHelper {
    private static final String TAG = "AudioRecordHelper";
    private int mBufferSize;
    private AudioRecord mAudioRecord;
    private IAudioMedia.AudioConfigure mConfigure;
    private boolean isRunning;

    private AudioRecordHelper() {
    }

    public static AudioRecordHelper get() {
        return SingletonHolder.instance;
    }

    private void init() {
        mConfigure = IAudioMedia.AudioConfigure.getDefault();
        int audioSource = mConfigure.getAudioSource();
        int sampleRate = mConfigure.getSampleRate();
        int chanelConfig = mConfigure.getChanelConfig();
        int audioFormat = mConfigure.getAudioFormat();
        mBufferSize = 2 * AudioRecord.getMinBufferSize(sampleRate, chanelConfig, audioFormat);
        mAudioRecord = new AudioRecord(audioSource, sampleRate, chanelConfig, audioFormat,
                mBufferSize);
    }

    public void startRecord(String path) {
        init();
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "audio record is not initialized");
            return;
        }
        if (isRunning) {
            Log.e(TAG, "audio record is running");
            return;
        }
        mAudioRecord.startRecording();
        isRunning = true;
        new RecordThread(path).start();
    }

    public void stopRecord() {
        isRunning = false;
        if (mAudioRecord != null) {
            try {
                mAudioRecord.stop();
                mAudioRecord.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class RecordThread extends Thread {

        private String path;

        public RecordThread(String path) {
            super();
            this.path = path;
        }

        @Override
        public void run() {
            try {
                File file = CameraUtils.createFile(path);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] data = new byte[mBufferSize];
                while (isRunning) {
                    if (mAudioRecord.read(data, 0, mBufferSize) != AudioRecord
                            .ERROR_INVALID_OPERATION) {
                        fos.write(data);
                    }
                }
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class SingletonHolder {
        private static final AudioRecordHelper instance = new AudioRecordHelper();
    }
}
