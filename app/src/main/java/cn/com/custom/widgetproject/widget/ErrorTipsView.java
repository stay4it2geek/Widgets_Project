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
public class ErrorTipsView extends RelativeLayout {
    Context context;
    View rootView;
    RelativeLayout rl_load_errorTip;
    private TextView tv_searh_state;//检索不到商品xxxx文字域
    LinearLayout loading_ball_ll;

    public ErrorTipsView(Context context) {
        super(context);
        initView(context);

    }

    public ErrorTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public ErrorTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        rootView = inflater.inflate(R.layout.customview_layout_loadview, this);
        rl_load_errorTip = (RelativeLayout) rootView.findViewById(R.id.rl_load_errorTip);
        tv_searh_state = (TextView) rootView.findViewById(R.id.tv_searh_state);
        loading_ball_ll = (LinearLayout) rootView.findViewById(R.id.loading_ball_ll);


    }


    /**
     * 设置加载消失，错误提示出现
     */
    public void setLoadGoneAndErrorTipsVisiable() {
        rl_load_errorTip.setVisibility(VISIBLE);
        loading_ball_ll.setVisibility(GONE);
        tv_searh_state.setText(" 加载失败,下拉重加载");
    }

    /**
     * 设置没有找到商品提示出现
     * @param keyword 商品关键词
     */
    public void setCannotSearchkeywordProductErrorShow(String keyword) {
        rl_load_errorTip.setVisibility(VISIBLE);
        loading_ball_ll.setVisibility(GONE);
        tv_searh_state.setText("没有找到商品" + "“" +keyword+ "”");
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
