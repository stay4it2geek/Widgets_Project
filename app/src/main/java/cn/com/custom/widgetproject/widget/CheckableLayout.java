package cn.com.custom.widgetproject.widget;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewParent;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.utils.BitmapLruCache;
import cn.com.custom.widgetproject.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 具有checked属性的自定义布局控件，包含了图片文字，图片可以从网络上下载
 */
public class CheckableLayout extends RelativeLayout implements CheckableView {
    OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private List<Checkable> checkables;
    private List<CheckableImageLayout> imageViews;
    private List<TextView> textViews;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    Context context;
    private boolean checked;
    String checkUrl;
    String uncheckUrl;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private boolean isNeedNetUrl;


    public CheckableLayout(Context context) {
        super(context);
        init(context);
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        this.context = context;
        checkables = new ArrayList<>();
        imageViews = new ArrayList<>();
        textViews = new ArrayList<>();
        mRequestQueue = getRequestQueue(context);
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        //使用可用缓存的8分之一进行缓存
        int cacheSize = 1024 * 1024 * memClass / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize, context));
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        this.setPadding(DisplayUtil.dip2Px(context, 12), DisplayUtil.dip2Px(context, 9), DisplayUtil.dip2Px(context, 12), DisplayUtil.dip2Px(context, 3));
    }


    public void setpara(String checkUrl, String uncheckUrl, String text, boolean isNeedNetUrl) {
        this.checkUrl = checkUrl;
        this.uncheckUrl = uncheckUrl;
        this.isNeedNetUrl = isNeedNetUrl;

        for (TextView textView : textViews) {
            textView.setText(text);
            if (checked) {
                textView.setTextColor(getResources().getColor(R.color.tabTextColor_red));
            } else {
                textView.setTextColor(getResources().getColor(R.color.tabTextcolor_gray));
            }

        }

        for (CheckableImageLayout imageView : imageViews) {
            imageView.setImageVisiable(checked, checkUrl, uncheckUrl, isNeedNetUrl, mImageLoader);

        }
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }


    @Override
    public void setChecked(boolean checked) {

        if (this.checked != checked) {
            for (Checkable checkable : checkables) {
                checkable.setChecked(checked);
            }
            for (CheckableImageLayout imageView : imageViews) {
                imageView.setImageVisiable(checked, checkUrl, uncheckUrl, isNeedNetUrl, mImageLoader);

            }
            for (TextView textView : textViews) {
                if (checked) {
                    textView.setTextColor(getResources().getColor(R.color.tabTextColor_red));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.tabTextcolor_gray));
                }

            }
            this.checked = checked;

            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, checked);
            }

            refreshDrawableState();
            invalidate();

        }
    }

    @Override
    public boolean performClick() {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof CheckableItemGroup && ((CheckableItemGroup) parent).isRadioGroupMode()) {
            setChecked(true);
        } else {
            setChecked(!checked);
        }
        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public boolean isContextClickable() {
        return true;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int children = getChildCount();
        for (int i = 0; i < children; i++) {
            View view = getChildAt(i);
            if (view instanceof Checkable) {
                Checkable checkable = (Checkable) view;
                view.setFocusableInTouchMode(false);
                view.setFocusable(false);
                view.setClickable(false);
                view.setDuplicateParentStateEnabled(false);
                checkables.add(checkable);
            } else if (view instanceof TextView) {
                textViews.add((TextView) view);
            } else if (view instanceof CheckableImageLayout) {
                imageViews.add((CheckableImageLayout) view);
            }
        }

    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener widgetListener) {
        mOnCheckedChangeWidgetListener = widgetListener;
    }


}
