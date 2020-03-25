package com.xyl.camera.video.view.opengl;

import com.xyl.camera.video.view.opengl.still.Triangle;

/**
 * author xiayanlei
 * date 2020/3/25
 * 绘制纹理
 */
public class GLDrawer {

    private int mTextureId;
    private Triangle mTriangle;

    public GLDrawer(int textureId) {
        this.mTextureId = textureId;
        mTriangle = new Triangle();
    }

    public void draw() {
        mTriangle.draw();
    }
}
