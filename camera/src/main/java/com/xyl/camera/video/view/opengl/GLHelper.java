package com.xyl.camera.video.view.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

/**
 * author xiayanlei
 * date 2020/3/25
 */
public class GLHelper {

    public static int createTextureId(boolean isOES) {
        if (isOES) {
            return createOESTextureId();
        }
        return create2DTextureId();
    }

    private static int create2DTextureId() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);//生成一个纹理
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);//绑定到外部纹理

        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return textures[0];
    }

    /**
     * @return 外部摄像头纹理
     */
    private static int createOESTextureId() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);//生成一个纹理
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);//绑定到外部纹理

        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return textures[0];
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);//创建着色器程序
        GLES20.glShaderSource(shader, shaderCode);//加载着色器
        GLES20.glCompileShader(shader);//编译着色器
        return shader;
    }

    public static float[] generateStandMatrix() {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        return matrix;
    }
}
