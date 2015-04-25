package com.pheebner.writingrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Objects;

/**
* Created by pjhee_000 on 3/29/2015.
*/
public class MyView extends View {

    private static int SUBMIT_MILLIS = 2000;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;

    private Handler handler;
    private Runnable r;

    private CoordList list;

    private class SubmitRunnable implements Runnable {

        @Override
        public void run() {
            int[] xCoords = new int[20];
            int[] yCoords = new int[20];

            list.standardize(xCoords, yCoords);

            resetCanvas();

            ((GraphicsActivity) getContext()).onSubmission(xCoords, yCoords);
        }
    }

    public MyView(Context c) {
        super(c);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        list = new CoordList();

        mPath = new Path();
        handler = new Handler();
        r = new SubmitRunnable();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public MyView(Context c, AttributeSet a) {
        this(c);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        resetList();
    }

    public void resetCanvas() {
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        resetList();

        invalidate();
    }

    public void resetList() {
        handler.removeCallbacks(r);
        list.init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void onTouchStart(float x, float y) {
        handler.removeCallbacks(r);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void onTouchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            list.addCoords(x, y);
            mX = x;
            mY = y;
        }
    }
    private void onTouchUp() {
        mPath.lineTo(mX, mY);
        list.addCoords(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw

        handler.postDelayed(r, SUBMIT_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp();
                invalidate();
                break;
        }
        return true;
    }
}
