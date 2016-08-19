package cn.com.custom.widgetproject.callback;

/**
 * 请求事件
 * Created by custom on 2016/6/14.
 */
public class ReuqstEvent {
    public String mMsg;//传递的信息
    public int cateGoryCode;//分类号

    public ReuqstEvent(String Message,int cate) {
        mMsg = Message;
        this.cateGoryCode =cate;

    }



}
