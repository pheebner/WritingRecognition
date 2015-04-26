package com.pheebner.writingrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
* Created by pjhee_000 on 3/29/2015.
*/
public class MyView extends View {

    public static int SUBMIT_MILLIS = 1000;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;

    private Handler handler;
    private Runnable r;

    private class SubmitRunnable implements Runnable {

        public int COORD_DIMEN = (int) Math.sqrt(GraphicsActivity.NUM_COORD_INPUT);
        public int PATH_COORD_SAMPLES = 101;
        public float PERCENT_COORD_PROGRESS = 1f / ((float) PATH_COORD_SAMPLES - 1);

        public int SECTOR_DIMEN = 4;
        public int PATH_SECTOR_SAMPLES = GraphicsActivity.NUM_SECTOR_INPUT;
        public float PERCENT_SECTOR_PROGRESS = 1 / ((float) GraphicsActivity.NUM_SECTOR_INPUT);

        @Override
        public void run() {
            handler.removeCallbacks(r);

            double[] inputs = new double[GraphicsActivity.NUM_INPUT];
            float width, height, largestX, largestY, smallestX, smallestY;
            int x, y;
            float[][] coords;

            PathMeasure measure = new PathMeasure(mPath, false);
            float length = measure.getLength();

            largestX = 0;
            largestY = 0;
            smallestX = Float.MAX_VALUE;
            smallestY = Float.MAX_VALUE;

            coords = new float[PATH_COORD_SAMPLES][2];

            for (int i = 0; i < PATH_COORD_SAMPLES; i++) {
                getCoords(i, coords[i], PERCENT_COORD_PROGRESS, measure, length);

                largestX = largestX > coords[i][0] ? largestX : coords[i][0];
                largestY = largestY > coords[i][1] ? largestY : coords[i][1];

                smallestX = smallestX < coords[i][0] ? smallestX : coords[i][0];
                smallestY = smallestY < coords[i][1] ? smallestY : coords[i][1];
            }

            width = (largestX - smallestX) + 1;
            height = (largestY - smallestY) + 1;

            for (int i = 0; i < PATH_COORD_SAMPLES; i++) {
                coords[i][0] -= smallestX;
                coords[i][1] -= smallestY;

                x = (int)(COORD_DIMEN * coords[i][0] / width);
                y = (int)(COORD_DIMEN * coords[i][1] / height);

                Log.d("OBS", "x " + x + " y " + y);
                inputs[x * COORD_DIMEN + y] = 1;
            }

            largestX = 0;
            largestY = 0;
            smallestX = Float.MAX_VALUE;
            smallestY = Float.MAX_VALUE;

            coords = new float[PATH_SECTOR_SAMPLES][2];

            for (int i = 0; i < PATH_SECTOR_SAMPLES; i++) {
                getCoords(i, coords[i], PERCENT_SECTOR_PROGRESS, measure, length);

                largestX = largestX > coords[i][0] ? largestX : coords[i][0];
                largestY = largestY > coords[i][1] ? largestY : coords[i][1];

                smallestX = smallestX < coords[i][0] ? smallestX : coords[i][0];
                smallestY = smallestY < coords[i][1] ? smallestY : coords[i][1];
            }

            width = (largestX - smallestX) + 1;
            height = (largestY - smallestY) + 1;

            for (int i = 0; i < GraphicsActivity.NUM_SECTOR_INPUT; i++) {
                coords[i][0] -= smallestX;
                coords[i][1] -= smallestY;

                x = (int)(SECTOR_DIMEN * coords[i][0] / width);
                y = (int)(SECTOR_DIMEN * coords[i][1] / height);

                inputs[GraphicsActivity.NUM_COORD_INPUT + i] = x * SECTOR_DIMEN + y;

                Log.d("OBS", "sector " + (GraphicsActivity.NUM_COORD_INPUT + i) + ": " + inputs[GraphicsActivity.NUM_COORD_INPUT + i]);
            }

            //output
//            for (int i = 0; i < GraphicsActivity.NUM_INPUT; i++) {
//                if (i % COORD_DIMEN == 0) Log.d("OBS", "COLUMN");
//                Log.d("OBS", inputs[i] + "");
//            }

            ((GraphicsActivity) getContext()).onSubmission(inputs);

            resetCanvas();
        }

        private void getCoords(int index, float[] dest, float coordProgress, PathMeasure measure, float pathLength) {
            float percent = index * coordProgress * pathLength;
            measure.getPosTan(percent, dest, null);
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
    }

    public void resetCanvas() {
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();

        invalidate();
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
        handler.removeCallbacks(r);

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void onTouchUp() {
        mPath.lineTo(mX, mY);
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
