package net.luculent.mediasample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.xyl.camera.audio.AudioRecordHelper;
import com.xyl.camera.audio.AudioTrackHelper;
import com.xyl.camera.audio.wav.PcmEncoder;

import net.luculent.mediasample.tools.FileUtils;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class AudioActivity extends AppCompatActivity implements CompoundButton
        .OnCheckedChangeListener, View.OnClickListener {

    private ToggleButton recordBtn;
    private ToggleButton playBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
    }

    private void initView() {
        recordBtn = findViewById(R.id.audio_record);
        recordBtn.setOnCheckedChangeListener(this);
        playBtn = findViewById(R.id.audio_track);
        playBtn.setOnCheckedChangeListener(this);
        findViewById(R.id.audio_encode).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.audio_record:
                toggleAudioRecord(isChecked);
                break;
            case R.id.audio_track:
                toggleAudioTrack(isChecked);
                break;
        }
    }

    private void toggleAudioRecord(boolean isChecked) {
        if (isChecked) {
            AudioRecordHelper.get().startRecord(FileUtils.getAudioFile());
        } else {
            AudioRecordHelper.get().stopRecord();
        }
    }

    private void toggleAudioTrack(boolean isChecked) {
        if (isChecked) {
            AudioTrackHelper.get().play(FileUtils.getAudioFile());
        } else {
            AudioTrackHelper.get().stop();
        }
    }
    

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.audio_encode:
                new PcmEncoder().setEncodeListener(new PcmEncoder.EncoderListener() {
                    @Override
                    public void onEncoded(boolean ret) {
                        Toast.makeText(v.getContext(), ret ? "转码成功" : "转码失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                }).encode(FileUtils.getAudioFile(), FileUtils.getAudioWavFile());
                break;
        }
    }
}
