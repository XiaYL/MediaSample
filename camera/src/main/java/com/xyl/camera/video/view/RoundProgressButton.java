package com.xyl.camera.video.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xyl.camera.video.IRoundProgressListener;

/**
 * author xiayanlei
 * date 2019/8/6
 */
public class RoundProgressButton extends View {

    private static final int INIT = 0;//初始化
    private static final int PRESS = 1;//长按
    private Paint solidPaint;
    private Paint strokePaint;
    private Paint progressPaint;
    private int state = INIT;
    private int progress = 0;
    private IRoundProgressListener progressListener;

    private GestureDetector gestureDetector;

    public RoundProgressButton(Context context) {
        this(context, null);
    }

    public RoundProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        solidPaint = new Paint();
        solidPaint.setColor(Color.WHITE);
        solidPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setColor(Color.LTGRAY);
        strokePaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint();
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStyle(Paint.Style.STROKE);

        gestureDetector = new GestureDetector(context, new SimpleGestureListener());

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isValidPointer(event)) {//不在点击区域内
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        if (state == PRESS) {
                            progressListener.onPressEnd();
                            endPress();
                        }
                        break;
                }
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public void setProgressListener(IRoundProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void endPress() {
        state = INIT;
        progress = 0;
        invalidate();
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public void setProgress(int progress, boolean animate) {
        if (state != PRESS) {
            return;
        }
        if (!animate) {
            this.progress = progress;
            invalidate();
        } else {
            ValueAnimator animator = ValueAnimator.ofInt(this.progress, progress);
            animator.setDuration(100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    RoundProgressButton.this.progress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }
    }

    private int outlineRadius = 0;
    private int centerX;
    private int centerY;


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int size = Math.min(getWidth(), getHeight());
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        int innerRadius;
        if (state == INIT) {
            innerRadius = size / 4;
            outlineRadius = size / 3;

            canvas.drawCircle(centerX, centerY, outlineRadius, strokePaint);
            canvas.drawCircle(centerX, centerY, innerRadius, solidPaint);
        } else if (state == PRESS) {
            innerRadius = size / 8;
            outlineRadius = size * 3 / 7;

            canvas.drawCircle(centerX, centerY, outlineRadius, solidPaint);
            canvas.drawCircle(centerX, centerY, innerRadius, strokePaint);

            int progressRadius = outlineRadius - 5;
            progressPaint.setStrokeWidth(10);
            RectF oval = new RectF(centerX - progressRadius, centerY - progressRadius, centerX + progressRadius, centerY + progressRadius);
            canvas.drawArc(oval, -90, 360 * progress / 100, false, progressPaint);
        }
    }

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (progressListener != null) {
                progressListener.onSingleTap();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (progressListener != null) {
                state = PRESS;
                progressListener.onPressStart();
            }
        }
    }

    private boolean isValidPointer(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        double calRadius = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return calRadius < outlineRadius;
    }
}
