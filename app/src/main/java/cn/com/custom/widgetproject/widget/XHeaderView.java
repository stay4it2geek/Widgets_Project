package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;
import cn.com.custom.widgetproject.utils.DevUtil;


/**
 * 头部加载控件
 */
public class XHeaderView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    private RoundProgressBar mProgressBar;//进度条

    private RotateAnimation refreshingAnimation;//加载动画

    private LinearLayout mContainer;

    private TextView mHintTextView;//提示文字

    private int mState = STATE_NORMAL;

    private boolean mIsFirst;
    private View jiantou;

    public XHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public XHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始化头部高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xscrolview_refresh_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
        jiantou = (View) findViewById(R.id.jiantou);
        mHintTextView = (TextView) findViewById(R.id.header_hint_text);
        mProgressBar = (RoundProgressBar) findViewById(R.id.refreshing_bar);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);

    }

    public void setState(int state) {
        if (state == mState && mIsFirst) {
            mIsFirst = true;
            return;
        }


        switch (state) {
            case STATE_NORMAL:

                mProgressBar.clearAnimation();
                mHintTextView.setText(R.string.pull_to_refresh);
                break;

            case STATE_READY:
                if (mState != STATE_READY) {
                    mHintTextView.setText(R.string.release_to_refresh);
                }
                break;

            case STATE_REFRESHING:
                mProgressBar.startAnimation(refreshingAnimation);
                mHintTextView.setText(R.string.refreshing);

                break;

            default:
                break;
        }

        mState = state;
    }

    /**
     * 设置头部可视高度
     *
     * @param height
     */
    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    /**
     * 获取头部可视高度
     *
     * @return
     */
    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

    boolean isFisrt = true;

    /**
     * 设置进度条进度
     *
     * @param progress
     */
    public void setprogress(int progress) {
        mProgressBar.setProgress(progress);
        DevUtil.e("FDSprogress",progress+"");

        if (progress >= 92) {
            //初始化
            if(jiantou.getVisibility()==VISIBLE) {
                Animation scaleAnimation = new ScaleAnimation(1f, 0.0f, 1f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //设置动画时间
                scaleAnimation.setDuration(150);
                scaleAnimation.setRepeatCount(0);
                jiantou.startAnimation(scaleAnimation);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        jiantou.setVisibility(INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }


        }else if(progress<=85){
            jiantou.setVisibility(VISIBLE);

        }

    }

}
