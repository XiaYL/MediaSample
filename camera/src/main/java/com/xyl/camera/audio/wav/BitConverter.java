package com.xyl.camera.audio.wav;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by xiayanlei on 2020/3/24.
 */

public class BitConverter {

    public static byte[] int2ByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    public static byte[] short2ByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }
}
