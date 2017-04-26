package item.finn.com.datacollection.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

/**
 * 
 * 代码中动态注册的广播会随着Activity的kill而kill,android系统在3.1版本以后为了加强系统安全性和优化性能对系统广播进行了限制
 * 
 * 或把广播放在service中，前台的任务管理并不会kill掉service。
 * 
 * 现在手机厂商各种定制导致原生的Android系统已经被改的面目全非了。可以考虑从linux层解决。
 * 
 * android5.0之前可以双进程
 * 
 *
 */
public class ScreenObserver {
	private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
	private ScreenStateListener mScreenStateListener;

	public ScreenObserver(Context context) {
		mContext = context;
		mScreenReceiver = new ScreenBroadcastReceiver();
	}

	public void startObserver(ScreenStateListener listener) {
		mScreenStateListener = listener;
		registerListener();
		getScreenState();
	}

	// public void shutdownObserver() {
	// unregisterListener();
	// }

	/**
	 * 获取screen状态
	 */
	@SuppressLint("NewApi")
	private void getScreenState() {
		if (mContext == null) {
			return;
		}

		PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		if (manager.isScreenOn()) {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOn();
			}
		} else {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOff();
			}
		}
	}

	private void registerListener() {
		if (mContext != null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			mContext.registerReceiver(mScreenReceiver, filter);
		}
	}

	private void unregisterListener() {
		if (mContext != null)
			mContext.unregisterReceiver(mScreenReceiver);
	}

	private class ScreenBroadcastReceiver extends BroadcastReceiver {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
				mScreenStateListener.onScreenOn();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
				mScreenStateListener.onScreenOff();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
				mScreenStateListener.onUserPresent();
			}
		}
	}

	public interface ScreenStateListener {// 返回给调用者屏幕状态信息
		public void onScreenOn();

		public void onScreenOff();

		public void onUserPresent();
	}
}