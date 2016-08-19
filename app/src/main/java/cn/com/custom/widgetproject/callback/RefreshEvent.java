package cn.com.custom.widgetproject.callback;

/**
 * 刷新事件
 * Created by custom on 2016/6/14.
 */
public class RefreshEvent {
    public String mMsg;//传递的信息
    public int cateCode;//分类号

    public RefreshEvent(String Message,int cateCode) {
        mMsg = Message;
        this.cateCode = cateCode;


    }



}
