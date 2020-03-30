package com.xyl.camera.video.view.opengl.drawer;

import android.graphics.SurfaceTexture;

/**
 * 视频渲染器
 */
public abstract class CameraDrawer extends StandardDrawer implements ICameraDrawer, SurfaceTexture
        .OnFrameAvailableListener {

    private SurfaceTexture mSurfaceTexture;

    @Override
    public boolean isOES() {
        return true;
    }

    @Override
    public void attach(int textureId) {
        super.attach(textureId);
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    public void drawPrepared() {

        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        //更新纹理
        mSurfaceTexture.updateTexImage();
        super.drawPrepared();
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public String getFragmentShaderCode() {//视频画面的渲染使用的是Android的拓展纹理
        //一定要加换行"\n"，否则会和下一行的precision混在一起，导致编译出错
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "varying vec2 vCoordinate;" +
                "uniform samplerExternalOES uTexture;" +//拓展纹理单元
                "void main() {" +
                "  gl_FragColor=texture2D(uTexture, vCoordinate);" +
                "}";
    }

}
