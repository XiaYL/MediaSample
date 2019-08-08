package net.luculent.mediasample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xyl.camera.video.ICaptureListener;
import com.xyl.camera.video.view.CaptureView;

import net.luculent.mediasample.tools.ActivityUtils;

import java.io.File;

/**
 * author xiayanlei
 * date 2019/8/5
 */
public class CaptureActivity extends AppCompatActivity {

    private CaptureView captureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityUtils.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        initView();
    }

    private void initView() {
        captureView = findViewById(R.id.capture_view);
        captureView.setResultListener(new ICaptureListener.IResultListener() {
            @Override
            public void onResult(File file, boolean isVideo) {
                Intent intent = new Intent();
                intent.putExtra("path", file.getPath());
                intent.putExtra("video", isVideo);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!captureView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
