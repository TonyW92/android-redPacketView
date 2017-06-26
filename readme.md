csdn���ĵ�ַ��http://blog.csdn.net/wz249863091/article/details/73744397


����֪ʶ
----
----------
</br>
ʹ��Xfermode�е�PorterDuffXfermodeʵ�����ǵĹν�Ч��
PorterDuffXfermode  ����һ���ǳ�ǿ���ת��ģʽ��ʹ����������ʹ��ͼ��ϳɵ�16��Porter-Duff���������һ��������Paint��������е�Canvasͼ����н�����
���������¹ٷ���Ч��ͼ
![Alt text](http://img.blog.csdn.net/20170626205836482?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3oyNDk4NjMwOTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
����Ͳ�һһ�����ˣ����ǲ��õ���DcIn��ģʽ--��Դͼ��Ŀ��ͼ�ཻ�ĵط���Ŀ��ͼ��
</br>
</br>
����ʵ��
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
 * ���Ч��
 *
 * @author tonywang
 */
public class RedPacketView extends View {
    private int mScaleTouchSlop;
    //�������ͼƬ
    private Bitmap mFgBitmap;
    //����ڵ���ͼƬ
    private Bitmap mBgBitmap;
    //�κ��·��
    private Path mPath;
    //�������ڵ���ͼƬ����ָ·���ཻЧ����canvas
    private Canvas mCanvas;
    //֮ǰ��x����
    private float preX;
	//֮ǰ��y����
    private float preY;
	//����Xfermode��paint
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
	    //���ϵͳ����С��Ч�������룬ϰ���ϲ���2������
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaleTouchSlop = 2 * viewConfiguration.getScaledTouchSlop();

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int screenW = wm.getDefaultDisplay().getWidth();
        int screenH = wm.getDefaultDisplay().getHeight();

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setARGB(128, 25, 0, 0);
        //�����������PorterDuffXfermode��ģʽָ��ΪDST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        //Ϊ��Ч����������ָ�������������Ӵ�ΪԲ��
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //���Ϊ50
        mPaint.setStrokeWidth(50);

        // ����ǰ��ͼBitmap
        mFgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        // ����ע�뻭��
        mCanvas = new Canvas(mFgBitmap);
        // ���ƻ�������Ϊ���Իң����Ҳ�����ǵ�Դͼ
        mCanvas.drawColor(0xFF808080);
        // ��ȡ������ͼBitmap
        mBgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.prize_none);
        // ���ű�����ͼBitmap����Ļ��С
        mBgBitmap = Bitmap.createScaledBitmap(mBgBitmap, screenW, screenH, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //���Ʊ���
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        //����ǰ��
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
        //��·��������Ŀ��ͼ
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
        //ǧ��ע�����invalidate��ʹview�ػ�
        invalidate();
        return true;
    }
}
```
100���д�������������ν�����
xml�Ͳ������ˣ�ֻҪ��һ��ViewGroup�����ؼ��ͺ���
</br>
������Ч��
</br>
![image](https://github.com/TonyW92/android-redPacketView/blob/master/effect_picture.gif)
</br>
�����������õ������Ŀ�У�����Ҫ��path�Ŀ����һ������