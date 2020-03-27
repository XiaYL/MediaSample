package com.xyl.camera.video.view.opengl.drawer;

/**
 * egl绘制器
 */
public interface IDrawer {

    void attach(int textureId);

    void draw();

    void release();
}
