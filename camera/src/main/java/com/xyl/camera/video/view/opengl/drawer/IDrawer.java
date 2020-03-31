package com.xyl.camera.video.view.opengl.drawer;

/**
 * egl绘制器
 */
public interface IDrawer {

    boolean isOES();

    void attach(int textureId);

    void draw();

    void release();

    void onSurfaceChanged(int w, int h);
}
