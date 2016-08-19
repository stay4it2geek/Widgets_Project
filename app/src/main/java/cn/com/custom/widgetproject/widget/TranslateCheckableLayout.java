package cn.com.custom.widgetproject.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewParent;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.custom.widgetproject.utils.DisplayUtil;

/**
 * 具有checked属性的自定义布局控件，包含了图片文字，图片可以从网络上下载
 * Created by walmstedt on 06.03.15.
 */
public class TranslateCheckableLayout extends RelativeLayout implements CheckableView {
    OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private List<Checkable> checkables;
    private List<RadioButton> radioButtones;
    private List<TextView> textViews;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    Context context;
    private boolean checked;


    public TranslateCheckableLayout(Context context) {
        super(context);
        init(context);
    }

    public TranslateCheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TranslateCheckableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TranslateCheckableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        this.context = context;
        checkables = new ArrayList<>();
        radioButtones = new ArrayList<>();
        textViews = new ArrayList<>();

        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        this.setPadding(DisplayUtil.dip2Px(context, 12), DisplayUtil.dip2Px(context, 9), DisplayUtil.dip2Px(context,12), DisplayUtil.dip2Px(context, 3));
    }






    @Override
    public void setChecked(boolean checked) {

        if (this.checked != checked) {
            for (Checkable checkable : checkables) {
                checkable.setChecked(checked);
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
            } else if (view instanceof RadioButton) {
                radioButtones.add((RadioButton) view);
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
