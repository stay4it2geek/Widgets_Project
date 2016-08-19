package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cn.com.custom.widgetproject.utils.DisplayUtil;

/**
 * 自适应SrcollerView的Viewpager类
 * Created by custom on 2016/6/12.
 */
public class AutoFitSrollerViewViewPager extends ViewPager {

    private int viewHeight;

    public AutoFitSrollerViewViewPager(Context context) {
        super(context);
    }

    public AutoFitSrollerViewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

        final View view = getChildAt(0);
        int width = getMeasuredWidth();

        if (view != null) {
            viewHeight = view.getMeasuredHeight();
        }

        if (wrapHeight) {
            // Keep the current measured width.
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        int fragmentHeight = measureFragment(((Fragment) getAdapter().instantiateItem(this, getCurrentItem())).getView());
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight + fragmentHeight+ (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DisplayUtil.dip2Px(getContext(),5), getResources().getDisplayMetrics()), MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }


}