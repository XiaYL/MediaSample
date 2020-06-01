package com.xyl.camera.video.view;

/**
 * author xiayanlei
 * date 2020/3/25
 */
public interface ICaptureView {

    void switchCamera();

    boolean onBackPressed();

    void onCaptureResult();

    void takePicture();

    void takeVideo();

    void stopRecord();
}
