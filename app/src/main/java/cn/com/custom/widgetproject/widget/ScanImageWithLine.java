package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 具有边角线条的扫描透明图
 * Created by custom on 2016/6/15.
 */
public class ScanImageWithLine extends ImageView {

    private Context context;

    public ScanImageWithLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ScanImageWithLine(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 251));//边角线条的颜色
        paint.setAntiAlias(true);
        paint.setStrokeWidth(t(3));

        canvas.drawLine(0, 0, 0, t(25), paint);//边角线条的长度和坐标
        canvas.drawLine(0, 0, t(25), 0, paint);//边角线条的长度和坐标

        canvas.drawLine(0, height - t(25), 0, height, paint);//边角线条的长度和坐标
        canvas.drawLine(0, height, t(25), height, paint);//边角线条的长度和坐标

        canvas.drawLine(width - t(25), 0, width, 0, paint);//边角线条的长度和坐标
        canvas.drawLine(width, 0, width, t(25), paint);//边角线条的长度和坐标

        canvas.drawLine(width, height - t(25), width, height, paint);//边角线条的长度和坐标
        canvas.drawLine(width - t(25), height, width, height, paint);//边角线条的长度和坐标
    }

    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public int t(float dpVal) {
        return dp2px(dpVal);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //   setMeasuredDimension(t(248),t(248));
    }
}
