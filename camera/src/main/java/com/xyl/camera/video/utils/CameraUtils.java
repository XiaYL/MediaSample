package com.xyl.camera.video.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author xiayanlei
 * date 2019/8/5
 */
public class CameraUtils {

    public static File savePhoto(byte[] data, float rotate, String path) {
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (rotate != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    matrix, true);
        }
        return saveBitmap2File(bm, path);
    }

    private static File saveBitmap2File(Bitmap bitmap, String outPath) {
        File file = createFile(outPath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                outputStream.write(baos.toByteArray());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeIO(outputStream);
        }
        return file;
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


    public static File getVideoThumb(Context context, String videoPath) {
        File file = new File(videoPath);
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(file.getPath());
        Bitmap bitmap = media.getFrameAtTime();
        String thumb = file.getName().substring(0, file.getName().lastIndexOf("."))
                .concat(".").concat("jpg");
        String cachePath = getCachePath(context, thumb);
        return saveBitmap2File(bitmap, cachePath);
    }

    public static String getCachePath(Context context, String filename) {
        return context.getCacheDir().getPath().concat("/").concat(filename);
    }

    private static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
