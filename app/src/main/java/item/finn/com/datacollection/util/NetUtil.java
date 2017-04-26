package item.finn.com.datacollection.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.text.TextUtils;

import item.finn.com.datacollection.GloableParams;

/**
 * 联网方式判断
 *
 */
public class NetUtil {
	/**
	 * 判断联网类型
	 * 
	 * @return
	 */
	public static boolean checkNet(Context context) {
		// 判断：是否使用WIFI联网
		// 判断：是否使用基站联网

		// 如果没有通过，提示用户进行网络设置

		// 如果使用的是基站进行联网，判断：Wap or Net
		// 读取代理信息：读取ip，如果ip不为null为Wap方式

		boolean isWifi = isWifiConnection(context);
		boolean isBaseStation = isBaseStationConnection(context);
		if (!isWifi && !isBaseStation) {
			return false;
		}

		if (isBaseStation) {
			String ip = Proxy.getDefaultHost();
			if (!TextUtils.isEmpty(ip)) {
				GloableParams.isWap = true;
			}
		}

		return true;
	}

	/**
	 * 是否使用基站联网
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isBaseStationConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 是否使用WIFI联网
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isWifiConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}
}
