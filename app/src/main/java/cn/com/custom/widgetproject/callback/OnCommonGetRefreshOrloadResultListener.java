package cn.com.custom.widgetproject.callback;

/**
 * 通用——获取刷新或加载结果的回调
 * Created by custom on 2016/7/14.
 */
public interface OnCommonGetRefreshOrloadResultListener {
    /**
     * 通用页刷新加载的回调
     */
    void onRefreshSuccese();

    void onLoadSuccese();

    void onErrorRefresh();

    void onErrorLoad();

    void onRefreshNull();

    void onLoadNull();

    void onRequestFail();
}
