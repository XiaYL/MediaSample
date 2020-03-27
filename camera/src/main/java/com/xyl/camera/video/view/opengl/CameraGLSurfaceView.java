package com.xyl.camera.video.view.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.xyl.camera.video.view.opengl.drawer.CameraDrawer;
import com.xyl.camera.video.view.opengl.drawer.ICameraDrawer;
import com.xyl.camera.video.view.opengl.render.CameraRender;

/**
 * author xiayanlei
 * date 2020/3/25
 */
public class CameraGLSurfaceView extends GLSurfaceView {

    private CameraRender mRender;

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
        ICameraDrawer mDrawer = new CameraDrawer() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        };
        setRenderer(mRender = new CameraRender(mDrawer));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mRender.release();
    }
}
