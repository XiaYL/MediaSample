package com.xyl.camera.video.view.opengl.drawer;

import android.opengl.GLES20;

import com.xyl.camera.video.view.opengl.GLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 世界坐标系:原点在中间,区间为(-1,1)
 * 纹理坐标系:原点在左上角,区间为(0,1)
 */
public abstract class AbsDrawer implements IDrawer {

    protected int mTextureId = -1;//纹理id
    protected int mProgram = -1;//程序id
    protected int mVertexPosHandler;//顶点坐标接受者
    protected int mTexturePosHandler;//纹理坐标接受者

    protected FloatBuffer mVertexBuffer;//顶点坐标缓冲区
    protected FloatBuffer mTextureBuffer;//纹理缓冲区

    public AbsDrawer() {
        mVertexBuffer = initBuffer(getVertexCoors());
        mTextureBuffer = initBuffer(getTextureCoors());
    }

    @Override
    public void attach(int textureId) {
        this.mTextureId = textureId;
        createEGLPrg();
    }

    @Override
    public void release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        GLES20.glDeleteProgram(mProgram);
    }

    /**
     * 创建OpenGL的绘制程序
     */
    public void createEGLPrg() {
        mProgram = GLES20.glCreateProgram();//创建OpenGL程序
        if (mProgram != -1) {
            int vertexShader = GLHelper.loadShader(GLES20.GL_VERTEX_SHADER, getVertexShaderCode());
            int fragmentShader = GLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShaderCode());

            GLES20.glAttachShader(mProgram, vertexShader);//加载着色器到程序
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);//连接到着色器程序
        }
    }


    /**
     * 将数据转换成floatbuffer供OpenGL es使用
     *
     * @param coors
     * @return
     */
    public FloatBuffer initBuffer(float[] coors) {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(coors.length * 4);
        // 数组排列用nativeOrder
        bb.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲区
        FloatBuffer floatBuffer = bb.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        floatBuffer.put(coors);
        // 设置缓冲区来读取第一个坐标
        floatBuffer.position(0);
        return floatBuffer;
    }


    /**
     * @return 顶点坐标, 默认铺满屏幕
     */
    public float[] getVertexCoors() {
        return new float[]{
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f
        };
    }

    /**
     * @return 顶点对应的纹理坐标
     */
    public float[] getTextureCoors() {
        return new float[]{
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
        };
    }

    public abstract String getVertexShaderCode();

    public abstract String getFragmentShaderCode();
}
