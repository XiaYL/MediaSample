package net.luculent.mediasample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.xyl.camera.video.view.MediaSurfaceView;

import net.luculent.mediasample.tools.ActivityUtils;

/**
 * author xiayanlei
 * date 2019/8/8
 */
public class MediaPlayActivity extends AppCompatActivity {

    private MediaSurfaceView mediaSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityUtils.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_paly);
        initView();
    }

    private void initView() {
        mediaSurfaceView = findViewById(R.id.media_view);
        mediaSurfaceView.play(getIntent().getStringExtra("path"));
    }
}
