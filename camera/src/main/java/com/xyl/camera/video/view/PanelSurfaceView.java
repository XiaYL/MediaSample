package com.xyl.camera.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author xiayanlei
 * date 2019/7/19
 */
public class PanelSurfaceView extends BaseSurfaceView implements Runnable {
    private Canvas mCanvas; // 声明一张画布
    private Paint p; // 声明一支画笔
    private String path;//图片路径

    private List<PathInfo> pathInfoList = new ArrayList<>();//所有轨迹
    private List<PathInfo> undoList = new ArrayList<>();//撤销的轨迹
    private Thread drawThread;
    private boolean lockCanvas = true;//true-不允许绘制，false-允许绘制

    private int mWidth;
    private int mHeight;

    public PanelSurfaceView(Context context) {
        this(context, null);
    }

    public PanelSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        p = new Paint(); // 创建一个画笔对象
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE); // 设置画笔的颜色为白色
        setFocusable(true); // 设置焦点
        setFocusableInTouchMode(true);
    }


    /**
     * 当SurfaceView创建的时候，调用此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        drawThread = new Thread(this);
        drawThread.start(); // 启动线程
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        drawThread = null;
        path = null;
    }

    /**
     * 当屏幕被触摸时调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); // 获得屏幕被触摸时对应的X轴坐标
        float y = event.getY(); // 获得屏幕被触摸时对应的Y轴坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addPath(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                pathInfoList.get(pathInfoList.size() - 1).lineTo(x, y);
                break;
        }
        return !lockCanvas;
    }

    private boolean shouldDraw = true;

    @Override
    public void run() {
        while (hasSurface && shouldDraw) {
            try {
                doDraw(); // 调用自定义画画方法
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(50); // 让线程休息50毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在画布上绘制轨迹
     */
    public void doDraw() {
        mCanvas = mHolder.lockCanvas(); // 获得画布对象，开始对画布画画
        if (mCanvas != null) {
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (path != null) {
                innerDrawBitmap();
            }
            for (int i = 0; i < pathInfoList.size(); i++) {
                PathInfo info = pathInfoList.get(i);
                if (info.use) {
                    mCanvas.drawPath(info.path, p);
                }
            }
            mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上
        }
        shouldDraw = !lockCanvas;
    }

    public void drawBitmap(String path) {
        this.path = path;
    }

    private void innerDrawBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int bmWidth = bitmap.getWidth();
        int bmHeight = bitmap.getHeight();
        float widthScale = 1.0f * mWidth / bmWidth;
        float heightScale = 1.0f * mHeight / bmHeight;
        float scale = Math.max(widthScale, heightScale);
        float dx = (mWidth - bmWidth * scale) / 2;
        float dy = (mHeight - bmHeight * scale) / 2;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);
        mCanvas.drawBitmap(bitmap, matrix, new Paint());
    }

    public void setLockCanvas(boolean lockCanvas) {
        this.lockCanvas = lockCanvas;
    }

    private static class PathInfo {
        Path path;
        boolean use;

        public PathInfo(Path path) {
            this.path = path;
            use = true;
        }

        public void moveTo(float x, float y) {
            path.moveTo(x, y);
        }

        public void lineTo(float x, float y) {
            path.lineTo(x, y);
        }
    }

    private void addPath(float x, float y) {
        if (undoList.size() > 0) {
            pathInfoList = pathInfoList.subList(0, pathInfoList.size() - undoList.size());
        }
        undoList.clear();
        PathInfo pathInfo = new PathInfo(new Path());
        pathInfo.moveTo(x, y);
        pathInfoList.add(pathInfo);
    }

    public void undo() {
        for (int i = pathInfoList.size() - 1; i >= 0; i--) {
            PathInfo info = pathInfoList.get(i);
            if (info.use) {
                info.use = false;
                undoList.add(info);
                break;
            }
        }
    }

    public void redo() {
        for (int i = undoList.size() - 1; i >= 0; i--) {
            PathInfo info = undoList.get(i);
            if (!info.use) {
                info.use = true;
                undoList.remove(i);
                break;
            }
        }
    }

    public void clear() {
        undoList.clear();
        pathInfoList.clear();
    }
}
