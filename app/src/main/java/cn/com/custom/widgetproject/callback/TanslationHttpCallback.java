package cn.com.custom.widgetproject.callback;

/**
 * 翻译请求回调
 * Created by Administrator on 2016/6/6 0006.
 */

public interface TanslationHttpCallback {
    /**
     *
     * @param result
     */
    void onTranslateSuccess(String result);

    void onTranslateFailure(String exception);
}
