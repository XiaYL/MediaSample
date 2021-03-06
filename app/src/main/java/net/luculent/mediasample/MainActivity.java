package net.luculent.mediasample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.xyl.camera.video.ICaptureListener;

import net.luculent.mediasample.permission.PermissionConstants;
import net.luculent.mediasample.permission.PermissionHelper;
import net.luculent.mediasample.tools.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MediaSample";

    private static final int REQUEST_CAPTURE = 1;

    private ImageView imageView;
    String path;
    boolean video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.capture_result);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview();
            }
        });
    }

    public void startCapture(View view) {
        PermissionHelper.getInstance()
                .permissions(
                        PermissionConstants.MICROPHONE,
                        PermissionConstants.CAMERA,
                        PermissionConstants.STORAGE,
                        PermissionConstants.PHONE
                )
                .callback(new PermissionHelper.SimplePermissionCallback() {
                    @Override
                    public void onPermissionDenied(String[] permissions, Boolean[] always) {
                        if (permissions.length == 0) {
                            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                            startActivityForResult(intent, REQUEST_CAPTURE);
//                            startActivity(new Intent(MainActivity.this,GLCaptureActivity.class));
                        }
                    }
                })
                .check(this)
        ;
    }

    public void startAudio(View view) {
        PermissionHelper.getInstance()
                .permissions(
                        PermissionConstants.MICROPHONE,
                        PermissionConstants.STORAGE
                        )
                .callback(new PermissionHelper.SimplePermissionCallback() {
                    @Override
                    public void onPermissionDenied(String[] permissions, Boolean[] always) {
                        if (permissions.length == 0) {
                            Intent intent = new Intent(MainActivity.this, AudioActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .check(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            path = data.getStringExtra(ICaptureListener.CAPTURE_PATH);
            video = data.getBooleanExtra(ICaptureListener.CAPTURE_VIDEO, true);
            String thumb = data.getStringExtra(ICaptureListener.CAPTURE_THUMB);
            ImageUtils.displayImage(imageView, video ? thumb : path);
        }
    }

    private void startPreview() {
        if (video) {
            Intent intent = new Intent(this, MediaPlayActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);
        }
    }
}
