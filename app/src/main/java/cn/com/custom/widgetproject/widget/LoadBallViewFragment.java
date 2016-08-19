package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.custom.widgetproject.R;

/**
 * 加载跳动小球
 * Created by custom on 2016/7/7.
 */
public class LoadBallViewFragment extends RelativeLayout {
    Context context;
    //左边跳动的小球
    ImageView ball_left;
    //右边跳动的小球
    ImageView ball_right;

    View rootView;
    RelativeLayout rl_load_errorTip;
    LinearLayout loading_ball_ll;
    private TextView tv_searh_state;//检索不到商品xxxx文字域

    public LoadBallViewFragment(Context context) {
        super(context);
        initView(context);

    }

    public LoadBallViewFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public LoadBallViewFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     *
     * @param context
     */
    private void initView(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.customview_layout_loadviewfragment, this);
        ball_left = (ImageView) rootView.findViewById(R.id.ball_left);
        ball_right = (ImageView) rootView.findViewById(R.id.ball_right);
        rl_load_errorTip = (RelativeLayout) rootView.findViewById(R.id.rl_load_errorTip);
        loading_ball_ll = (LinearLayout) rootView.findViewById(R.id.loading_ball_ll);
        tv_searh_state = (TextView) rootView.findViewById(R.id.tv_searh_state);


        animaiton();


    }

    /**
     * 启动动画
     */
    public void animaiton() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.25f, Animation.RELATIVE_TO_PARENT, 0.1f);
        animation.setDuration(250);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        ball_left.startAnimation(animation);

        TranslateAnimation animation2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.1f, Animation.RELATIVE_TO_PARENT, -0.25f);
        animation2.setDuration(250);
        animation2.setRepeatCount(-1);
        animation2.setRepeatMode(Animation.REVERSE);
        ball_right.startAnimation(animation2);
    }

    /**
     * 设置加载消失，错误提示出现
     */
    public void setLoadGoneAndErrorTipsVisiable() {
        rl_load_errorTip.setVisibility(VISIBLE);
        tv_searh_state.setText(" 加载失败,下拉重加载");
        loading_ball_ll.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置没有找到商品提示出现
     * @param keyword 商品关键词
     */
    public void setCannotSearchkeywordProductErrorShow(String keyword) {
        rl_load_errorTip.setVisibility(VISIBLE);
        tv_searh_state.setText("没有找到商品" + "“" +keyword+ "”");
        loading_ball_ll.setVisibility(View.INVISIBLE);
    }
    public void setErrorGone() {
        rl_load_errorTip.setVisibility(INVISIBLE);
        loading_ball_ll.setVisibility(View.VISIBLE);

    }

    /**
     * 获取错误提示是否已经出现的标志值
     * @return true 为可见 false为不可见
     */
    public boolean isErrrorTipsVisiable() {
        if (rl_load_errorTip.getVisibility() == VISIBLE) {
            return true;
        }
        return false;
    }


}
