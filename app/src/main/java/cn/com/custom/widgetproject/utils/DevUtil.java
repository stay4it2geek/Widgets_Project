package cn.com.custom.widgetproject.utils;
import android.util.Log;

import cn.com.custom.widgetproject.config.BuildConfig;



/**
 * 开发调试工具类
 * DevUtil
 */
public final class DevUtil {

    //Release发布时DEBUG必须设定为false
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static void printLog(int priority, String tag, String msg) {
//        Log.e("DEBUG",DEBUG+"");
        if (DEBUG) {
            Log.println(priority, tag, msg);
        }
    }

    private static void printLog(int priority, String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.println(priority, tag, msg + '\n' + Log.getStackTraceString(tr));
        }
    }

    public static final String Util_LOG = makeLogTag(FileUtils.class);

    public static String makeLogTag(Class<?> cls) {
        return cls.getName();
    }
    public static void v(String tag, String msg) {
        printLog(Log.VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        printLog(Log.VERBOSE, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        printLog(Log.DEBUG, tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        printLog(Log.DEBUG, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        printLog(Log.INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        printLog(Log.INFO, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        printLog(Log.WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        printLog(Log.WARN, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        printLog(Log.ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        printLog(Log.ERROR, tag, msg, tr);
    }

}
