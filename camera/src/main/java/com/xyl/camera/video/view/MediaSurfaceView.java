package com.xyl.camera.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.xyl.camera.video.IMediaListener;
import com.xyl.camera.video.utils.MediaPlayerHelper;

/**
 * author xiayanlei
 * date 2019/7/25
 */
public class MediaSurfaceView extends BaseSurfaceView {
    private MediaPlayerHelper mPlayerHelper;
    private String path;

    public MediaSurfaceView(Context context) {
        this(context, null);
    }

    public MediaSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mPlayerHelper = new MediaPlayerHelper();
    }

    public void setMediaListener(IMediaListener mediaListener) {
        mPlayerHelper.setMediaListener(mediaListener);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mPlayerHelper.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        if (path != null) {
            play(path);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mPlayerHelper.release();
    }

    public void play(String path) {//等待surface创建以后才能播放视频
        this.path = path;
        if (!hasSurface) {
            return;
        }
        mPlayerHelper.play(path);
    }

    public void pause() {
        mPlayerHelper.pause();
    }
}
