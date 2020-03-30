package com.xyl.camera.video.view.opengl.drawer;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/**
 * 图片绘制
 */
public class BitmapDrawer extends StandardDrawer {

    private Bitmap mBitmap;

    public BitmapDrawer(Bitmap bitmap) {
        super();
        mBitmap = bitmap;
    }

    @Override
    public void init() {
        super.init();
        Matrix.scaleM(mMVPMatrix, 0, 1, -1, 1);
    }

    /**
     * 准备数据,有三步操作,获取位置句柄,启用句柄,设置位置数据
     */
    @Override
    public void drawPrepared() {
        //更新纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        super.drawPrepared();
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
