package com.xyl.camera.video.view.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * author xiayanlei
 * date 2020/3/25
 */
public class CameraGLSurfaceView extends GLSurfaceView {

    private GLRender mGlRender;

    public CameraGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setEGLContextClientVersion(2);//设置egl使用2.0
        setRenderer(mGlRender = new GLRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
