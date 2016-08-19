package cn.com.custom.widgetproject.callback;

/**
 * 推荐 ——获取刷新或加载结果的回调
 * Created by custom on 2016/7/14.
 */
public interface OnRecomondGetRefreshResultListener {

    /**
     * 推荐页刷新完成的回调
     *
     */
    void onRecomondRefreshFinish();//刷新结束

    void onRecomondRefreshError();//刷新出错

    void onRecomondRefreshNull();//刷新没有了
}
