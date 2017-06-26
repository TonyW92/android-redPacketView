package com.example.tony.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * 红包效果
 *
 * @author tonywang
 */
public class RedPacketView extends View {
    private int mScaleTouchSlop;
    //红包内容图片
    private Bitmap mFgBitmap;
    //红包遮挡物图片
    private Bitmap mBgBitmap;
    //刮红包路径
    private Path mPath;
    private Canvas mCanvas;
    private float preX;
    private float preY;

    private Paint mPaint;

    public RedPacketView(Context context) {
        super(context);
        init(context);
    }

    public RedPacketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaleTouchSlop = viewConfiguration.getScaledTouchSlop();

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int screenW = wm.getDefaultDisplay().getWidth();
        int screenH = wm.getDefaultDisplay().getHeight();

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setARGB(128, 25, 0, 0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(50);

        // 生成前景图Bitmap
        mFgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        // 将其注入画布
        mCanvas = new Canvas(mFgBitmap);
        // 绘制画布背景为中性灰
        mCanvas.drawColor(0xFF808080);
        // 获取背景底图Bitmap
        mBgBitmap = (BitmapFactory.decodeResource(context.getResources(), R.drawable.success));
        // 缩放背景底图Bitmap至屏幕大小
        mBgBitmap = (Bitmap.createScaledBitmap(mBgBitmap, screenW, screenH, true));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        //绘制前景
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
        //画路径
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);
                float dy = Math.abs(y - preY);
                if (dx > mScaleTouchSlop || dy > mScaleTouchSlop) {
                    mPath.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    preX = x;
                    preY = y;
                }
                break;
        }
        invalidate();
        return true;
    }
}
