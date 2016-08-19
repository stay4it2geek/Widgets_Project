package cn.com.custom.widgetproject.callback;

/**
 * 加载事件
 * Created by custom on 2016/6/14.
 */
public class LoadEvent {
    public String mMsg;//传递的信息
    public int cateCode;//分类号

    public LoadEvent(String Message,int cateCode) {
        mMsg = Message;
        this.cateCode = cateCode;


    }



}
