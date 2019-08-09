package com.xyl.camera.video.utils;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xyl.camera.video.ICaptureListener;

import java.io.File;
import java.io.IOException;

/**
 * author xiayanlei
 * date 2019/8/6
 */
public class MediaRecordHelper {

    private static final String TAG = "MediaRecordHelper";

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private String filepath;
    private int duration = -1;
    private int rotation;
    private File mOutputFile;
    private ICaptureListener.IRecordListener recordListener;
    private boolean isRecording;

    //计时器，到指定的时间结束录制
    private CountDownTimer countDownTimer;

    public MediaRecordHelper(@NonNull Camera mCamera) {
        this.mCamera = mCamera;
        parameters = mCamera.getParameters();
    }

    public void setRecordListener(ICaptureListener.IRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void startRecord(String filepath, int duration) {
        this.filepath = filepath;
        if (duration != -1) {
            this.duration = duration + 1000;
        }
        new MediaPrepareTask().execute();
    }

    public void recordFinish() {
        stopRecord();
        if (mOutputFile != null && recordListener != null) {
            recordListener.onRecordFinish(mOutputFile);
        }
    }

    public void stopRecord() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (mMediaRecorder == null) {
            return;
        }
        // stop recording and release camera
        try {
            mMediaRecorder.stop();  // stop the recording
        } catch (RuntimeException e) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
        }
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            initCountDownTimer();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();
            } else {
                if (mOutputFile != null) {
                    mOutputFile.delete();
                    mOutputFile = null;
                }
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            isRecording = result;
            if (recordListener != null) {
                if (result) {
                    if (countDownTimer != null) {
                        countDownTimer.start();
                    }
                    recordListener.onRecordStart();
                } else {
                    recordListener.onRecordError();
                }
            }
        }
    }

    private void initCountDownTimer() {
        if (duration == -1) {
            return;
        }
        countDownTimer = new CountDownTimer(duration, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (recordListener != null) {
                    int progress = (int) (100 - 100 * millisUntilFinished / duration);
                    recordListener.onRecordProgress(progress);
                }
            }

            @Override
            public void onFinish() {
                if (recordListener != null) {
                    recordListener.onRecordProgress(100);
                }
                recordFinish();
            }
        };
    }

    private boolean prepareVideoRecorder() {

        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOrientationHint(rotation);//设置视频数据旋转矩阵

        // Use the same size for recording profile.
        Camera.Size previewSize = parameters.getPreviewSize();
        RecordingConfig config = RecordingConfig.get();
        config.videoFrameWidth = previewSize.width;
        config.videoFrameHeight = previewSize.height;

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        if (profile != null) {
            profile.videoFrameRate = config.videoFrameRate;
            profile.videoBitRate = config.videoBitRate;
            profile.fileFormat = config.outputFormat;
            profile.videoCodec = config.videoCodec;
            profile.audioCodec = config.audioCodec;
            mMediaRecorder.setProfile(profile);
        } else {
            mMediaRecorder.setOutputFormat(config.outputFormat);
            mMediaRecorder.setVideoFrameRate(config.videoFrameRate);
            mMediaRecorder.setVideoSize(config.videoFrameWidth, config.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(config.videoBitRate);
            mMediaRecorder.setVideoEncoder(config.videoCodec);
            mMediaRecorder.setAudioEncoder(config.audioCodec);
        }

        // Step 4: Set output file
        mOutputFile = CameraUtils.createFile(filepath);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());
        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        isRecording = false;
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public static class RecordingConfig {
        private int outputFormat;
        private int videoFrameRate;
        private int videoFrameWidth;
        private int videoFrameHeight;
        private int videoBitRate;
        private int videoCodec;
        private int audioCodec;

        private RecordingConfig() {
            outputFormat = MediaRecorder.OutputFormat.MPEG_4;
            videoCodec = MediaRecorder.VideoEncoder.H264;
            audioCodec = MediaRecorder.AudioEncoder.AAC;
            videoFrameRate = 15;
            videoBitRate = 1024 * 1024;
        }

        public static RecordingConfig get() {
            return new RecordingConfig();
        }
    }
}
