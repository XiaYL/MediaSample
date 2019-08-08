package com.xyl.camera.video.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author xiayanlei
 * date 2019/8/5
 */
public class CameraUtils {

    public static File savePhoto(byte[] data, float rotate, String path) {
        File file = createFile(path);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            if (rotate == 0) {
                outputStream.write(data);
            } else {
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                Bitmap dst = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                        matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                dst.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                outputStream.write(baos.toByteArray());
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File createFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
