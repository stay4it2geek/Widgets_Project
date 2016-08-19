package cn.com.custom.widgetproject.callback;

import cn.com.custom.widgetproject.widget.LoadBallViewFragment;
import cn.com.custom.widgetproject.widget.LoadingBallView;

/**
 * 通用 _请求结果的回调
 * Created by custom on 2016/7/14.
 */
public interface OnCommonResultListener {
    /**
     * 通用页请求、刷新、加载的回调
     */

    void onCommonRequestSuccese(boolean isCannotLoadMore);

    void onCommonLoadMoreSuccese(boolean isCannotLoadMore);

    void onCommonCanNotLoadMore();

    void onCommonReFreshSuccese(boolean isCannotLoadMore);//刷新成功

    void onCommonRequestFail(LoadBallViewFragment loadingBallView);//请求失败

    void onCommonReFreshFail(LoadBallViewFragment loadingBallView);//刷新失败

    void onCommonLoadMoreFail();//加载下一页失败
}
