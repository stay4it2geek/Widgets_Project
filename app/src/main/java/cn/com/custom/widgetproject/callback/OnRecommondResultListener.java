package cn.com.custom.widgetproject.callback;

import cn.com.custom.widgetproject.widget.LoadBallViewFragment;
import cn.com.custom.widgetproject.widget.LoadingBallView;

/**
 * 推荐 ——请求结果的回调
 * Created by custom on 2016/7/14.
 */
public interface OnRecommondResultListener {

    /**
     * 推荐完成的回调
     *
     */
    void onReCommondTabSelect(int position);//

    void onReCommondReFreshSuccese();//刷新成功
    void onReCommondRequestSuccese();//请求成功
    void onReCommondRequestFail(LoadBallViewFragment loadingBallView);//请求失败
    void onReCommondReFreshFail(LoadBallViewFragment loadingBallView);//刷新失败

}
