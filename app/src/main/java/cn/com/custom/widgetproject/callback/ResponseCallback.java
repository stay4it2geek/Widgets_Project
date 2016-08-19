package cn.com.custom.widgetproject.callback;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * ApiManager需要调用和返回的数据模型泛型回调Callback
 *
  * Created by custom on 2016/6/6.
 * @param <T> 返回Response类型指定
 */
public interface ResponseCallback<T> {
    /**
     * 请求失败
     *
     * @param request request对象
     * @param e       异常
     */
  public  void onFailure(Request request, Exception e);

    /**
     * 请求成功
     *
     * @param response response对象
     * @param model    指定数据模型
     */
  public  void onResponse(Response response, T model);
}
