package cn.com.custom.widgetproject.utils;

/**
 * 字符串判断处理工具类
 * Created by custom on 2016/06/02
 *
 */
public class StringUtil {
    /**
     * 字符串为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return (string == null || string.length() == 0|| "".equals(string));
    }


    /**
     * 比较2个字符串是否相同，相同则返回true，不同则返回false
     *
     * @param factString
     * @param formString
     * @return
     */
    public static boolean isEqual(String factString, String formString) {
        boolean equalFlag = false;
        if (!StringUtil.isEmpty(factString) && !StringUtil.isEmpty(formString)) {
            equalFlag = factString.equals(formString);
        }

        return equalFlag;
    }


}

