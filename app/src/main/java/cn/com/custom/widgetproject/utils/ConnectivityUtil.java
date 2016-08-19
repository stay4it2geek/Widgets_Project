package cn.com.custom.widgetproject.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 
 * 网络连接工具类
 * 
 * @author linwm
 * 
 */
public class ConnectivityUtil {
	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return 返回网络状态 true 具有网络 false没有网络
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * 判断是否有WIFI网络连接
	 * 
	 * @param context
	 * @return 返回网络状态 true 具有WIFI网络 false没有WIFI网络
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * 判断是否有数据流量网络连接
	 * 
	 * @param context
	 * @return 返回网络状态 true 具有数据流量网络 false没有数据流量网络
	 */
	public static boolean isMobileConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return (networkInfo != null && networkInfo.isConnected());
	}




	public static boolean checkNetwork(Context getActivity) {
			if (!ConnectivityUtil.isOnline(getActivity)
					&& !ConnectivityUtil.isWifiConnected(getActivity)
					&& !ConnectivityUtil.isMobileConnected(getActivity)) {
				return false;
			}

		return true;
	}
}
