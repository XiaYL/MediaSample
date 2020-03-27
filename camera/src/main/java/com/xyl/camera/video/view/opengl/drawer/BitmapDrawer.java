package com.xyl.camera.video.view.opengl.drawer;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * 图片绘制
 */
public class BitmapDrawer extends AbsDrawer {

    private Bitmap mBitmap;

    public BitmapDrawer(Bitmap bitmap) {
        super();
        mBitmap = bitmap;
    }

    @Override
    public void draw() {
        if (mProgram == -1) {
            return;
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
        drawPrepared();//奇怪的地方,如果将此方法单独抽象出来使用,在子类里面不能显示完整
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    /**
     * 准备数据,有三步操作,获取位置句柄,启用句柄,设置位置数据
     */
    private void drawPrepared() {

        //更新纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        //获取顶点着色器的位置的句柄
        mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        //设置坐标数据
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);

        mTexturePosHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(mTexturePosHandler, 0);
    }

    @Override
    public String getVertexShaderCode() {
        return "attribute vec4 aPosition;" +//顶点坐标
                "attribute vec2 aCoordinate;" +//纹理坐标
                "varying vec2 vCoordinate;" +//用于传递纹理坐标给片元着色器，命名和片元着色器中的一致
                "void main() {" +
                "  gl_Position = aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}";
    }

    @Override
    public String getFragmentShaderCode() {
        return "precision mediump float;" +//配置float精度，使用了float数据一定要配置：lowp(低)/mediump(中)/highp(高)
                "uniform sampler2D uTexture;" +//从Java传递进入来的纹理单元
                "varying vec2 vCoordinate;" +//从顶点着色器传递进来的纹理坐标
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +//根据纹理坐标，从纹理单元中取色
                "  gl_FragColor = color;" +
                "}";
    }
}
