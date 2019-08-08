package com.xyl.camera.video;

import android.media.MediaPlayer;

/**
 * author xiayanlei
 * date 2019/8/6
 */
public interface IMediaListener {
    void onComplete(MediaPlayer mp);

    void onPrepared(int duration);
}
