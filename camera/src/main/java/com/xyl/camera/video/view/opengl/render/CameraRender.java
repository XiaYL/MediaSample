package com.xyl.camera.video.view.opengl.render;

import com.xyl.camera.video.utils.CameraHelper;
import com.xyl.camera.video.view.opengl.GLHelper;
import com.xyl.camera.video.view.opengl.drawer.ICameraDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender extends BaseRender<ICameraDrawer> {

    private CameraHelper mCameraHelper;

    public CameraRender(ICameraDrawer drawer) {
        super(drawer);
        mCameraHelper = CameraHelper.get();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = GLHelper.createOESTextureId();
        mDrawer.attach(mTextureId);
        mCameraHelper.open();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        mCameraHelper.startPreview(mDrawer.getSurfaceTexture());
    }

    @Override
    public void release() {
        super.release();
        mCameraHelper.close();
    }
}
