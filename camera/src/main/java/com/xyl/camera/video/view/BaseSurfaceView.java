package com.xyl.camera.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * author xiayanlei
 * date 2019/7/25
 */
public class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "BaseSurfaceView";

    protected SurfaceHolder mHolder;
    protected boolean hasSurface;

    public BaseSurfaceView(Context context) {
        this(context, null);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        mHolder = getHolder();//获得SurfaceHolder对象
        mHolder.addCallback(this);// 为SurfaceView添加状态监听
    }

    /**
     * 当SurfaceView创建的时候，调用此函数
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated: ");
        hasSurface = true;
    }

    /**
     * 当SurfaceView的视图发生改变的时候，调用此函数
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged: ");
    }

    /**
     * 当SurfaceView销毁的时候，调用此函数
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed: ");
        hasSurface = false;
    }
}
