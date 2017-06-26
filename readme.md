csdn博文地址：http://blog.csdn.net/wz249863091/article/details/73744397


背景知识
----
----------
</br>
使用Xfermode中的PorterDuffXfermode实现我们的刮奖效果
PorterDuffXfermode  这是一个非常强大的转换模式，使用它，可以使用图像合成的16条Porter-Duff规则的任意一条来控制Paint如何与已有的Canvas图像进行交互。
我们来看下官方的效果图
![Alt text](http://img.blog.csdn.net/20170626205836482?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3oyNDk4NjMwOTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
这里就不一一讲述了，我们采用的是DcIn的模式--在源图和目标图相交的地方画目标图像
</br>
</br>
代码实现
----
----------
</br>

```
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
    //用于来遮挡物图片和手指路径相交效果的canvas
    private Canvas mCanvas;
    //之前的x坐标
    private float preX;
	//之前的y坐标
    private float preY;
	//采用Xfermode的paint
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
	    //获得系统的最小有效滑动距离，习惯上采用2倍距离
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaleTouchSlop = 2 * viewConfiguration.getScaledTouchSlop();

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int screenW = wm.getDefaultDisplay().getWidth();
        int screenH = wm.getDefaultDisplay().getHeight();

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setARGB(128, 25, 0, 0);
        //这里就是设置PorterDuffXfermode，模式指定为DST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        //为了效果更贴近手指滑动，设置连接处为圆形
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //宽度为50
        mPaint.setStrokeWidth(50);

        // 生成前景图Bitmap
        mFgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        // 将其注入画布
        mCanvas = new Canvas(mFgBitmap);
        // 绘制画布背景为中性灰，这个也是我们的源图
        mCanvas.drawColor(0xFF808080);
        // 获取背景底图Bitmap
        mBgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.prize_none);
        // 缩放背景底图Bitmap至屏幕大小
        mBgBitmap = Bitmap.createScaledBitmap(mBgBitmap, screenW, screenH, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        //绘制前景
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
        //画路径，绘制目标图
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
        //千万注意加上invalidate，使view重绘
        invalidate();
        return true;
    }
}
```
100多行代码就能完成这个刮奖动画
xml就不介绍了，只要在一个ViewGroup里加入控件就好了
</br>
来看看效果
</br>
![image](https://github.com/TonyW92/android-redPacketView/blob/master/effect_picture.gif)
</br>
如果你想把他用到你的项目中，还需要对path的宽度做一个适配