package com.xyl.camera.audio.wav;

/**
 * Created by xiayanlei on 2020/3/24.
 * wav的音频头文件
 * 原始的pcm格式的文件需要做处理才能转换成wav格式,可以参考：http://soundfile.sapp.org/doc/WaveFormat
 */

public class WavFileHeader {

    public static final int CHUNK_SIZE_OFFSET = 4;
    public static final int CHUNK_SIZE_EXCLUDE_DATA = 36;
    public static final int SUB_CHUNK2_SIZE_OFFSET = 40;

    //第一部分，文件格式描述
    public String mChunkID = "RIFF";
    public int mChunkSize;// 36 + SubChunk2Size
    public String mFormat = "wave";

    //第二部分，fmt，音频文件的详细参数
    public String mSubChunk1ID = "fmt";
    public int mSubChunk1Size = 16;// 16 for pcm
    public short mAudioFormat = 1;//pcm = 1
    public short mNumChannels = 1;//mono = 1,stereo = 2
    public int mSampleRate = 8000;
    public int mByteRate;// ==mSampleRate*mNumChannels*mBitsPerSample/8
    public short mBlockAlign;// ==*mNumChannels*mBitsPerSample/8
    public short mBitsPerSample = 8;

    //第三部分，原始数据
    public String mSubChunk2ID = "data";
    public int mSubChunk2Size = 0;// ==NumSamples*mNumChannels*mBitsPerSample/8

    public WavFileHeader(int sampleRate, int bitsPerSample, int channels) {
        this.mSampleRate = sampleRate;
        this.mBitsPerSample = (short) bitsPerSample;
        this.mNumChannels = (short) channels;
        mByteRate = mSampleRate * mNumChannels * mBitsPerSample / 8;
        mBlockAlign = (short) (mNumChannels * mBitsPerSample / 8);
    }
}
