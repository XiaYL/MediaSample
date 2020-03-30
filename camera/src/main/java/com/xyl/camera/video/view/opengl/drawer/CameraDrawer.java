package com.xyl.camera.video.view.opengl.drawer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * 视频渲染器
 */
public abstract class CameraDrawer extends AbsDrawer implements ICameraDrawer, SurfaceTexture
        .OnFrameAvailableListener {

    private static final int GL_MATRIX_SIZE = 16;
    private SurfaceTexture mSurfaceTexture;

    private float[] mMVPMatrix = new float[GL_MATRIX_SIZE];
    private float[] mSTMatrix = new float[GL_MATRIX_SIZE];
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;

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
        Matrix.setIdentityM(mMVPMatrix, 0);

        //更新纹理
        mSurfaceTexture.updateTexImage();

        //获取顶点着色器的位置的句柄
        mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        //设置坐标数据
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0,
                mVertexBuffer);

        mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0,
                mTextureBuffer);

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public String getVertexShaderCode() {
        return "uniform mat4 uMVPMatrix;" +
                "uniform mat4 uSTMatrix;" +
                "attribute vec4 aPosition;" +//顶点坐标
                "attribute vec4 aCoordinate;" +//纹理坐标
                "varying vec2 vCoordinate;" +//用于传递纹理坐标给片元着色器，命名和片元着色器中的一致
                "void main() {" +
                "  gl_Position = uMVPMatrix * aPosition;" +
                "  vCoordinate = (uSTMatrix * aCoordinate).xy;" +
                "}";
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
