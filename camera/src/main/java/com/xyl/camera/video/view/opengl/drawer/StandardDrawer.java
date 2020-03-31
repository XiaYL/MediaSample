package com.xyl.camera.video.view.opengl.drawer;


import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.xyl.camera.video.view.opengl.GLHelper;

import java.util.Arrays;

/**
 * 世界坐标系:原点在中间,区间为(-1,1)
 * 纹理坐标系:原点在左下角,区间为(0,1)
 */
public abstract class StandardDrawer extends AbsDrawer implements IStandardDrawer {

    protected float[] mMVPMatrix;//标准坐标变换矩阵
    protected float[] mSTMatrix;//纹理坐标变换矩阵
    protected int muMVPMatrixHandle;
    protected int muSTMatrixHandle;
    protected int mObjectWidth;//真实的宽度
    protected int mObjectHeight;//真实的高度

    @Override
    public void init() {
        super.init();
        mMVPMatrix = GLHelper.generateStandMatrix();
        mSTMatrix = GLHelper.generateStandMatrix();
    }

    @Override
    public void createEGLPrg() {
        super.createEGLPrg();
        if (mProgram != -1) {
            //获取顶点着色器的位置的句柄
            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        }
    }

    @Override
    public void drawPrepared() {

        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        //设置坐标数据
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0,
                mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0,
                mTextureBuffer);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
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
    public void onObjectChanged(int w, int h) {
        this.mObjectWidth = w;
        this.mObjectHeight = h;
    }

    /**
     * @return 正交投影矩阵
     */
    public float[] getProjectionMatrix() {
        float[] prjMatrix = GLHelper.generateStandMatrix();
        if (mSurfaceWidth <= 0 || mSurfaceHeight <= 0 || mObjectWidth <= 0 || mObjectHeight <= 0) {
            return prjMatrix;
        }
        float surfaceRatio = (float) mSurfaceWidth / mSurfaceHeight;
        float realRatio = (float) mObjectWidth / mObjectHeight;
        float aspect = 1;
        if (realRatio > surfaceRatio) {
            aspect = realRatio / surfaceRatio;
            Matrix.orthoM(
                    prjMatrix, 0,
                    -1f, 1f,
                    -aspect, aspect,
                    -1f, 1f
            );
        } else {// 原始比例小于窗口比例，缩放高度会导致高度超出，因此，高度以窗口为准，缩放宽度
            aspect = surfaceRatio / realRatio;
            Matrix.orthoM(
                    prjMatrix, 0,
                    -aspect, aspect,
                    -1f, 1f,
                    -1f, 1f
            );
        }
        Log.i("abc", "getProjectionMatrix: " + Arrays.toString(prjMatrix));
        return prjMatrix;
    }
}
