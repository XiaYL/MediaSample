package net.luculent.mediasample;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xyl.camera.video.view.opengl.drawer.BitmapDrawer;
import com.xyl.camera.video.view.opengl.drawer.IDrawer;
import com.xyl.camera.video.view.opengl.render.BaseRender;

import net.luculent.mediasample.tools.ActivityUtils;

/**
 * author xiayanlei
 * date 2020/3/25
 */
public class GLCaptureActivity extends AppCompatActivity {

    private GLSurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityUtils.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glcapture);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.gl_surface);
        surfaceView.setEGLContextClientVersion(2);//设置egl使用2.0
        IDrawer drawer = new BitmapDrawer(BitmapFactory.decodeResource(getResources(), R.mipmap.img_00));
        surfaceView.setRenderer(new BaseRender(drawer));
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
