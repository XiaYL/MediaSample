package com.xyl.camera.video.view.opengl.drawer;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.xyl.camera.video.view.opengl.GLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 世界坐标系:原点在中间,区间为(-1,1),最终需要做垂直翻转
 * 纹理坐标系:原点在左下角,区间为(0,1)
 */
public abstract class AbsDrawer implements IDrawer {

    protected int mSurfaceWidth = 0;//画布宽度
    protected int mSurfaceHeight = 0;//画布高度

    protected int mTextureId = -1;//纹理id
    protected int mProgram = -1;//程序id
    protected int mVertexPosHandler;//顶点坐标接受者
    protected int mTexturePosHandler;//纹理坐标接受者

    protected FloatBuffer mVertexBuffer;//顶点坐标缓冲区
    protected FloatBuffer mTextureBuffer;//纹理缓冲区
    protected int mTarget;
    private int mVertexCount;

    public AbsDrawer() {
        this(4);
    }

    public AbsDrawer(int vertexCount) {
        this.mVertexCount = vertexCount;
        init();
    }

    public void init() {
        mVertexBuffer = initBuffer(getVertexCoors());
        mTextureBuffer = initBuffer(getTextureCoors());
        mTarget = isOES() ? GLES11Ext.GL_TEXTURE_EXTERNAL_OES : GLES20.GL_TEXTURE_2D;
    }

    @Override
    public boolean isOES() {
        return false;
    }

    @Override
    public void attach(int textureId) {
        this.mTextureId = textureId;
        createEGLPrg();
    }

    @Override
    public void draw() {
        if (mProgram == -1) {
            return;
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(mTarget, mTextureId);//绑定到外部纹理
        drawPrepared();//奇怪的地方,如果将此方法单独抽象出来使用,在子类里面不能显示完整
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCount);
    }

    public abstract void drawPrepared();

    @Override
    public void release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(mTarget, 0);
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        GLES20.glDeleteProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(int w, int h) {
        this.mSurfaceWidth = w;
        this.mSurfaceHeight = h;
    }

    /**
     * 创建OpenGL的绘制程序
     */
    public void createEGLPrg() {
        mProgram = GLES20.glCreateProgram();//创建OpenGL程序
        if (mProgram != -1) {
            int vertexShader = GLHelper.loadShader(GLES20.GL_VERTEX_SHADER, getVertexShaderCode());
            int fragmentShader = GLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER,
                    getFragmentShaderCode());

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
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f
        };
    }

    public abstract String getVertexShaderCode();

    public abstract String getFragmentShaderCode();
}
