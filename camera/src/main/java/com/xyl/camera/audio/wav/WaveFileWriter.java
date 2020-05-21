package com.xyl.camera.audio.wav;

import com.xyl.camera.audio.IAudioMedia;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by xiayanlei on 2020/3/24.
 */

public class WaveFileWriter {

    private DataOutputStream mDos;

    private int sampleRate;
    private int bitsPerSample;
    private int channels;
    private String filepath;
    private int mDataSize;//原始文件的大小

    public WaveFileWriter() {
        IAudioMedia.AudioConfigure configure = IAudioMedia.AudioConfigure.getDefault();
        this.sampleRate = configure.getSampleRate();
        this.bitsPerSample = configure.getBitsPerSample();
        this.channels = configure.getChannelSize();
    }

    public WaveFileWriter(int sampleRate, int bitsPerSample, int channels) {
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
        this.channels = channels;
    }

    public WaveFileWriter open(String filepath) throws Exception {
        this.filepath = filepath;
        FileOutputStream fos = new FileOutputStream(filepath);
        mDos = new DataOutputStream(fos);
        writeHeader();
        return this;
    }

    /**
     * pcm文件转换成wav文件
     *
     * @param pcmFile
     */
    public boolean convertPcm2Wav(String pcmFile) throws Exception {
        FileInputStream is = new FileInputStream(pcmFile);
        byte[] data = new byte[4096];
        mDataSize = 0;
        int count;
        while ((count = is.read(data)) != -1) {
            mDos.write(data);
            mDataSize += data.length;
        }
        is.close();
        writeDataSize();
        return true;
    }

    private void writeHeader() {
        WavFileHeader header = new WavFileHeader(sampleRate, bitsPerSample, channels);
        try {
            mDos.writeBytes(header.mChunkID);
            mDos.write(BitConverter.int2ByteArray(header.mChunkSize), 0, 4);
            mDos.writeBytes(header.mFormat);

            mDos.writeBytes(header.mSubChunk1ID);
            mDos.write(BitConverter.int2ByteArray(header.mSubChunk1Size), 0, 4);
            mDos.write(BitConverter.short2ByteArray(header.mAudioFormat), 0, 2);
            mDos.write(BitConverter.short2ByteArray(header.mNumChannels), 0, 2);
            mDos.write(BitConverter.int2ByteArray(header.mSampleRate), 0, 4);
            mDos.write(BitConverter.int2ByteArray(header.mByteRate), 0, 4);
            mDos.write(BitConverter.short2ByteArray(header.mBlockAlign), 0, 2);
            mDos.write(BitConverter.short2ByteArray(header.mBitsPerSample), 0, 2);

            mDos.writeBytes(header.mSubChunk2ID);
            mDos.write(BitConverter.int2ByteArray(header.mSubChunk2Size), 0, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataSize() throws Exception {
        if (mDos == null) {
            return;
        }
        RandomAccessFile wvaFile = new RandomAccessFile(filepath, "rw");
        wvaFile.seek(WavFileHeader.CHUNK_SIZE_OFFSET);
        wvaFile.write(BitConverter.int2ByteArray(WavFileHeader.CHUNK_SIZE_EXCLUDE_DATA +
                mDataSize), 0, 4);
        wvaFile.seek(WavFileHeader.SUB_CHUNK2_SIZE_OFFSET);
        wvaFile.write(BitConverter.int2ByteArray(mDataSize), 0, 4);
        wvaFile.close();
        mDos.close();
    }
}
