package com.xyl.camera.video.view.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.xyl.camera.video.utils.CameraHelper;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author xiayanlei
 * date 2020/3/25
 * 流程：opengl > textureId > surfaceTexture(预览camera)
 */
public class GLRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "GLRender";
    private CameraHelper mCameraHelper;

    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private GLDrawer mGlDrawer;
    private WeakReference<GLSurfaceView> mGLSurfaceViewRef;

    public GLRender(GLSurfaceView glSurfaceView) {
        this.mGLSurfaceViewRef = new WeakReference<>(glSurfaceView);
        mCameraHelper = CameraHelper.get();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        mTextureId = GLHelper.createTextureId();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mGlDrawer = new GLDrawer(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraHelper.open();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: ");
        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
        GLES20.glViewport(0, 0, width, height);
        mCameraHelper.startPreview(mSurfaceTexture);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        Log.i(TAG, "onDrawFrame: ");
        // 设置白色为清屏
//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 更新纹理
        mSurfaceTexture.updateTexImage();

        mGlDrawer.draw();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onFrameAvailable: ");
        GLSurfaceView surfaceView = mGLSurfaceViewRef.get();
        if (surfaceView != null) {
            surfaceView.requestRender();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
