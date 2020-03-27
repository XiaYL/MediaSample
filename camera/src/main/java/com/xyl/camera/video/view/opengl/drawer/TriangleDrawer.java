package com.xyl.camera.video.view.opengl.drawer;

import android.opengl.GLES20;

/**
 * 三角形绘制
 */
public class TriangleDrawer extends AbsDrawer {

    // Set color with red, green, blue and alpha (opacity) values
    private float color[] = {1.0f, 0.0f, 0.0f, 1.0f};


    private int mColorPosHandler;


    @Override
    public void draw() {
        if (mProgram == -1) {
            return;
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
        drawPrepared();
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }

    /**
     * 准备数据,有三步操作,获取位置句柄,启用句柄,设置位置数据
     */
    private void drawPrepared() {
        //获取顶点着色器的位置的句柄
        mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        //设置坐标数据
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);

        mColorPosHandler = GLES20.glGetUniformLocation(mProgram, "aColor");
        GLES20.glUniform4fv(mColorPosHandler, 1, color, 0);
    }

    @Override
    public float[] getVertexCoors() {
        return new float[]{//三角形定点坐标
                -1f, -1f,
                1f, -1f,
                0f, 1f
        };
    }

    @Override
    public float[] getTextureCoors() {
        return new float[]{//三角形对应的纹理坐标
                0f, 1f,
                1f, 1f,
                05f, 0
        };
    }

    @Override
    public String getVertexShaderCode() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "}";
    }

    @Override
    public String getFragmentShaderCode() {
        return "precision mediump float;" +
                "uniform vec4 aColor;" +
                "void main() {" +
                "  gl_FragColor = aColor;" +
                "}";
    }
}
