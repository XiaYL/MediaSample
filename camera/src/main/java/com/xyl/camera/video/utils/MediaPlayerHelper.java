package com.xyl.camera.video.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import com.xyl.camera.video.IMediaListener;

/**
 * author xiayanlei
 * date 2019/8/8
 */
public class MediaPlayerHelper {
    private static final String TAG = "MediaPlayerHelper";
    private MediaPlayer mediaPlayer;
    private IMediaListener mediaListener;
    private String path;

    public MediaPlayerHelper() {
        initMedia();
    }

    private void initMedia() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setLooping(true);//没有起作用，暂时从回调里面处理循环播放
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {//播放完毕回调
                if (mediaListener != null) {
                    mediaListener.onComplete(mp);
                }
            }
        });
    }

    public void setMediaListener(IMediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }

    public void setDisplay(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    public void play(String path) {//等待surface创建以后才能播放视频
        this.path = path;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    if (mediaListener != null) {
                        mediaListener.onPrepared(mp.getDuration() / 1000);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "play: ", e);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
        path = null;
    }
}
