package com.xyl.camera.video.view.opengl.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.xyl.camera.video.view.opengl.GLHelper;
import com.xyl.camera.video.view.opengl.drawer.IDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRender implements GLSurfaceView.Renderer {

    protected IDrawer mDrawer;
    protected int mTextureId = -1;

    public BaseRender(IDrawer drawer) {
        this.mDrawer = drawer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);//清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTextureId = GLHelper.createTextureId();
        mDrawer.attach(mTextureId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mDrawer != null) {
            mDrawer.draw();
        }
    }
}
