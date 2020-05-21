package com.xyl.camera.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;

/**
 * Created by xiayanlei on 2020/3/5.
 */

public interface IAudioMedia {

    /**
     * @param path 录制文件的保存路径
     */
    void start(String path);

    void stop();

    interface OnCompleteListener {
        void onCompleted();
    }

    class AudioConfigure {
        int audioSource;
        int sampleRate;
        int chanelConfig;
        int audioFormat;

        int streamType;

        public static Builder DEFAULT_BUILDER =
                new Builder()
                        .audioSource(MediaRecorder.AudioSource.MIC)
                        .sampleRate(44100)
                        .chanelConfig(AudioFormat.CHANNEL_IN_MONO)
                        .audioFormat(AudioFormat.ENCODING_PCM_16BIT)
                        .streamType(AudioManager.STREAM_MUSIC);

        public static AudioConfigure getDefault() {
            return DEFAULT_BUILDER
                    .build();
        }

        private AudioConfigure(Builder builder) {
            this.audioSource = builder.audioSource;
            this.sampleRate = builder.sampleRate;
            this.chanelConfig = builder.chanelConfig;
            this.audioFormat = builder.audioFormat;
            this.streamType = builder.streamType;
        }

        public int getAudioSource() {
            return audioSource;
        }

        public int getSampleRate() {
            return sampleRate;
        }

        public int getChanelConfig() {
            return chanelConfig;
        }

        public int getAudioFormat() {
            return audioFormat;
        }

        public int getStreamType() {
            return streamType;
        }

        public int getBitsPerSample() {
            if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                return 16;
            }
            return 8;
        }

        public int getChannelSize() {
            if (chanelConfig == AudioFormat.CHANNEL_IN_STEREO) {
                return 2;
            }
            return 1;
        }

        public static class Builder {
            int audioSource;
            int sampleRate;
            int chanelConfig;
            int audioFormat;

            int streamType;

            public Builder audioSource(int audioSource) {
                this.audioSource = audioSource;
                return this;
            }

            public Builder sampleRate(int sampleRate) {
                this.sampleRate = sampleRate;
                return this;
            }

            public Builder chanelConfig(int chanelConfig) {
                this.chanelConfig = chanelConfig;
                return this;
            }

            public Builder audioFormat(int audioFormat) {
                this.audioFormat = audioFormat;
                return this;
            }

            public Builder streamType(int streamType) {
                this.streamType = streamType;
                return this;
            }

            public AudioConfigure build() {
                return new AudioConfigure(this);
            }
        }
    }
}
