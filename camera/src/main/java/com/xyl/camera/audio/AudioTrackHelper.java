package com.xyl.camera.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xyl.camera.video.utils.CameraUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class AudioTrackHelper {
    private static final String TAG = "AudioTrackHelper";
    private int mBufferSize;
    private AudioTrack mAudioTrack;
    private IAudioMedia.AudioConfigure mConfigure;
    private boolean isRunning;
    private Handler mHandler;

    private AudioTrackHelper() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static AudioTrackHelper get() {
        return SingletonHolder.instance;
    }

    private void init() {
        mConfigure = IAudioMedia.AudioConfigure.DEFAULT_BUILDER
                .chanelConfig(AudioFormat.CHANNEL_OUT_MONO)
                .build();
        int sampleRate = mConfigure.getSampleRate();
        int chanelConfig = mConfigure.getChanelConfig();
        int audioFormat = mConfigure.getAudioFormat();
        mBufferSize = 2 * AudioTrack.getMinBufferSize(sampleRate, chanelConfig, audioFormat);
        mAudioTrack = new AudioTrack(mConfigure.getStreamType(), sampleRate,
                chanelConfig, audioFormat, mBufferSize, AudioTrack.MODE_STREAM);
    }

    public void play(String path) {
        init();
        if (mAudioTrack.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "audio record is not initialized");
            return;
        }
        if (isRunning) {
            Log.e(TAG, "audio record is running");
            return;
        }
        isRunning = true;
        new AudioTrackThread(path).start();
    }

    public void stop() {
        isRunning = false;
        if (mAudioTrack != null) {
            try {
                mAudioTrack.stop();
                mAudioTrack.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AudioTrackThread extends Thread {

        private String path;

        public AudioTrackThread(String path) {
            super();
            this.path = path;
        }

        @Override
        public void run() {
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            try {
                FileInputStream is = new FileInputStream(file);
                byte[] data = new byte[mBufferSize];
                int byteCount;
                while (isRunning && (byteCount = is.read(data)) != -1) {
                    mAudioTrack.write(data, 0, byteCount);
                    mAudioTrack.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                notifyComplete();
            }
        }
    }

    private void notifyComplete() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "playing complete: " + CameraUtils.isMainThread());
            }
        });
    }

    private static class SingletonHolder {
        private static final AudioTrackHelper instance = new AudioTrackHelper();
    }
}
