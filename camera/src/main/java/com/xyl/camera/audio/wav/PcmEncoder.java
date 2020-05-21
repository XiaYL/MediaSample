package com.xyl.camera.audio.wav;

import android.os.Handler;
import android.os.Looper;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class PcmEncoder {

    private Handler mHandler;
    private EncoderListener mEncodeListener;


    public PcmEncoder() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public PcmEncoder setEncodeListener(EncoderListener encodeListener) {
        this.mEncodeListener = encodeListener;
        return this;
    }

    public void encode(final String pcmFile, final String wavFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WaveFileWriter fileWriter = new WaveFileWriter();
                    boolean ret = fileWriter.open(wavFile).convertPcm2Wav(pcmFile);
                    notifyResult(ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void notifyResult(final boolean ret) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEncodeListener != null) {
                    mEncodeListener.onEncoded(ret);
                }
            }
        });
    }

    public interface EncoderListener {
        void onEncoded(boolean ret);
    }
}
