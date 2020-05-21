package net.luculent.mediasample.tools;

import android.os.Environment;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class FileUtils {

    public static String getAudioFile() {
        String path = Environment.getExternalStorageDirectory() + "/xyl/audio" + "/audiorecord.pcm";
        return path;
    }

    public static String getAudioWavFile() {
        String path = Environment.getExternalStorageDirectory() + "/xyl/audio" + "/audio.wav";
        return path;
    }
}
