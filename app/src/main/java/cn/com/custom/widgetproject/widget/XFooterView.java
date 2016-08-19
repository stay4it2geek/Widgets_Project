package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;


/**
 *底部加载控件
 */
public class XFooterView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_NOMORE = 3;


    private View mLayout;

    private RoundProgressBar mProgressBar;
    private TextView mHintView;
    private int mState = STATE_NORMAL;
    private RotateAnimation loadingAnimation;

    public XFooterView(Context context) {
        super(context);
        initView(context);
    }

    public XFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mLayout = LayoutInflater.from(context).inflate(R.layout.xscrollview_load_footer, null);
        mLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        addView(mLayout);

        mHintView = (TextView) mLayout.findViewById(R.id.footer_hint_text);
        mProgressBar = (RoundProgressBar) mLayout.findViewById(R.id.footer_progressbar);
        mHintView = (TextView) mLayout.findViewById(R.id.footer_hint_text);

        loadingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);
        // 添加匀速转动动画
        LinearInterpolator lir = new LinearInterpolator();
        loadingAnimation.setInterpolator(lir);

    }

    /**
     *设置状态
     *
     * @param state
     * @see #STATE_LOADING
     * @see #STATE_NORMAL
     * @see #STATE_READY
     */
    public void setState(int state) {
        Log.e("state", state + "");
        if (state == STATE_LOADING||state == STATE_NORMAL) {
            mProgressBar.setProgress(92);
            mProgressBar.setVisibility(View.VISIBLE);
            mHintView.setVisibility(View.INVISIBLE);
        } else if (state == STATE_NOMORE) {
            mHintView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                mProgressBar.startAnimation(loadingAnimation);
                break;
            case STATE_NOMORE:
                mProgressBar.setVisibility(GONE);
                mProgressBar.clearAnimation();
                mHintView.setText(R.string.load_null);
                break;
            case STATE_READY:
                break;

            case STATE_LOADING:
                mProgressBar.startAnimation(loadingAnimation);
                break;
        }

        mState = state;
    }

    /**
     * 设置底部距离
     *
     * @param margin
     */
    public void setBottomMargin(int margin) {
        if (margin < 0) return;
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        lp.bottomMargin = margin;
        mLayout.setLayoutParams(lp);
    }

    /**
     * 获取底部距离
     *
     * @return
     */
    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        return lp.bottomMargin;
    }



    /**
     * 当底部不可操作时隐藏
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        lp.height = 0;
        mLayout.setLayoutParams(lp);
    }

    /**
     * 显示底部
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mLayout.setLayoutParams(lp);
    }


}
