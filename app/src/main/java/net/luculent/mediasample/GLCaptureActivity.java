package net.luculent.mediasample;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
    }
}
