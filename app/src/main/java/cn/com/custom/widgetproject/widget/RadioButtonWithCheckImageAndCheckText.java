package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.utils.DisplayUtil;


/**
 * 具有图片和文字check属性的RadioButton
 * Created by custom on 2016/6/16.
 */
public class RadioButtonWithCheckImageAndCheckText extends android.widget.RadioButton {

    public RadioButtonWithCheckImageAndCheckText(Context context) {
        super(context);
    }

    public RadioButtonWithCheckImageAndCheckText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
    }

    public RadioButtonWithCheckImageAndCheckText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     *设置radioButton的drawableTop图片
     * @param normalDrawable  没有选择状态的图片
     * @param checkedDrawable  选择状态的图片
     */
    public void setDrawableTopIcon(Drawable normalDrawable, Drawable checkedDrawable) {
        invalidate();
        //设置图片居顶部
        this.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        this.setPadding(0, DisplayUtil.dip2Px(getContext(),9), 0, DisplayUtil.dip2Px(getContext(),7));
        this.setCompoundDrawablePadding(DisplayUtil.dip2Px(getContext(),3));
        this.setCompoundDrawablesWithIntrinsicBounds(null, newSelector(getContext(), normalDrawable, checkedDrawable), null, null);


    }

   /*private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        Bitmap newbmp = null;

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        if (width > 0 && height > 0) {
            float scaleWidth = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidth, scaleHeight);
            newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                    matrix, true);
        }
        return new BitmapDrawable(null, newbmp);
    }*/

   /*private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            if (width > 0 && height > 0) {
                bitmap = Bitmap.createBitmap(width, height, config);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }
        }
        return bitmap;
    }*/

    /**
     *  设置图片选择器Selector
     * @param context  上下文环境
     * @param normalDrawable  没有选择状态的图片
     * @param checkedDrawable  选择状态的图片
     * @return  具有状态的drawable图片
     */
    public static StateListDrawable newSelector(Context context, Drawable normalDrawable, Drawable checkedDrawable) {
        StateListDrawable bg = new StateListDrawable();
        /**
         * addState中的new int[｛a,b｝]中的a可以是多种状态
         */
        bg.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        bg.addState(new int[]{}, normalDrawable);
        return bg;
    }


}
