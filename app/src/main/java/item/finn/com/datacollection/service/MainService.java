package item.finn.com.datacollection.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import item.finn.com.datacollection.GloableParams;
import item.finn.com.datacollection.db.dao.RunningDataDao;
import item.finn.com.datacollection.receiver.ScreenReceiver;
import item.finn.com.datacollection.util.NetUtil;
import item.finn.com.datacollection.util.PrintUtil;

/**
 * 1.注册监听 2部署传感器相关任务 3 初始化Socekt服务 4服务被杀死后重启
 */
@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class MainService extends Service implements SensorEventListener {
	@SuppressLint("SimpleDateFormat")
	private LocationManager lm;
	private SensorManager sensorManager;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 日志名称格式
	private double maxaccelerated;
	private double maxcentacceleration;
	private LosLocationListener listener;
	private String provider;
	private SocketService socketService;

	private float[] gravity = new float[3]; // 重力在设备x、y、z轴上的分量
	private float[] motion = new float[3]; // 过滤掉重力后，加速度在x、y、z上的分量
	private double ratioY;
	private double angle;
	private int counter = 1;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		register(); // 注册屏幕&GPS状态监听&网络监听
		deploySensorTask(); // 部署数据采集任务
		new InitSocketThread().start();// 初始化Socekt服务
		// daemonProcess();

	}

	class InitSocketThread extends Thread {
		public void run() {
			try {
				socketService = new SocketService(MainService.this);
				socketService.init();
			} catch (Exception e) {
				PrintUtil.printException2Error(e);
			}
		}
	}

	private void register() {
		/**
		 * 屏幕点亮监听
		 */
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(new ScreenReceiver(), filter);
		/**
		 * GPS开关状态监听
		 */
		registerReceiver(new GpsStatusReceiver(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

		/**
		 * 网络监听
		 */
		ConnectionChangeReceiver myReceiver = new ConnectionChangeReceiver();
		registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	/**
	 * 初始化定位,获取加速位&陀螺仪的服务
	 */
	public void deploySensorTask() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = lm.getBestProvider(criteria, true);
		listener = new LosLocationListener();
		lm.requestLocationUpdates(provider, 0, 0, listener);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度
		Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);// 陀螺仪
		sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

	private class LosLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			// float accuracy = location.getAccuracy();//精度
			// double altitude = location.getAltitude();//海拔
			float bearing = location.getBearing();// 角度
			double gpsSpeed = (location.hasSpeed() && location.getSpeed() * 3.6 >= 5) ? location.getSpeed() * 3.6 : 0;
			String data = longitude + "," + latitude + "," + bearing + "," + gpsSpeed + "," + sdf.format(new Date())
					+ "," + 0 + "," + 0 + "," + maxaccelerated + "," + maxcentacceleration;
			// Log.i("test", data);
			RunningDataDao.initRunningDataDao(getApplicationContext());
			RunningDataDao.getInstance().addToSocketPacket(data);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	}

	private class GpsStatusReceiver extends BroadcastReceiver {
		// @Override
		public void onReceive(Context context, Intent intent) {

			if (lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// gps 打开
				lm.requestLocationUpdates(provider, 0, 0, listener);
			} else {
				lm.removeUpdates(listener);
			}

		}
	}

	/**
	 * 确保服务杀死后能够重启
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	class ConnectionChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (NetUtil.checkNet(context)) { // 网络开关状态由"关闭-->打开"的广播事件
				}
			} catch (Exception e) {
				PrintUtil.printException2Error(e);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (lm != null) {
			lm.removeUpdates(listener);
		}
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
	}

	/**
	 * **********************************************************
	 * 对于陀螺仪，测量的是x、y、z三个轴向的角速度，分别从values[0]、values[1]、values[2]中读取，单位为弧度/秒。
	 * 在华为P6的机器上，陀螺仪非常敏感，平放在桌面，由于电脑照成的轻微震动在不断地刷屏，为了避免写UI造成的性能问题，只写Log。
	 * 陀螺仪可以测量运动过程中的角速度，但要测量手机本身的角度，这靠的是加速仪器，因为有重力方向作为校准
	 * ***********************************************************
	 */

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();
		if (type == Sensor.TYPE_GYROSCOPE) {
			if (counter++ % 10 == 0) {
				// Log.i("gyroscope", "事件：" + " x:" + event.values[0] + " y:" +
				// event.values[1] + " z:" + event.values[2]);
			}
		} else if (type == Sensor.TYPE_ACCELEROMETER) {
			// 对加速度测量仪的x，y，z数值进行修正处理，减去重力加速度，得到我们这个惯性系的加速度，并根据设备的Y轴与垂直地面方向（重力反方向）的夹角

			for (int i = 0; i < 3; i++) {
				/*
				 * accelermeter是很敏感的，因为重力是恒力们 移动设备，它的变化不会太快，
				 * 不象摇晃手机这样的外力那样突然。因此通过lowpassfilter对重力进行过滤
				 * 这个低通滤波器的权重，使用了0.1和0.9，也可以设置为0.2和0.8。
				 */
				gravity[i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]);
				motion[i] = event.values[i] - gravity[i];
			}

			// 计算重力在Y轴方向的量，即G*cos(α)
			ratioY = gravity[1] / SensorManager.GRAVITY_EARTH;
			if (ratioY > 1.0)
				ratioY = 1.0;
			if (ratioY < -1.0)
				ratioY = -1.0;
			// 获得α的值，根据z轴的方向修正其正负值。
			angle = Math.toDegrees(Math.acos(ratioY));
			if (gravity[2] < 0)
				angle = -angle;

			// 避免频繁扫屏，每10次打印一次值
			if (counter++ % 10 == 0) {
				// Log.i("accelerometer", "Raw Values : \n" + "   x,y,z = " +
				// event.values[0] + "," + event.values[1]
				// + "," + event.values[2] + "\n" + "Gravity values : \n" +
				// "   x,y,z = " + gravity[0] + ","
				// + gravity[1] + "," + gravity[2] + "\n" + "Motion values : \n"
				// + "   x,y,z = " + motion[0] + ","
				// + motion[1] + "," + motion[2] + "\n" + "Y轴角度 :" + angle);
				counter = 1;
			}
			// motion[3]是过滤重力后的数值，如果各值如果非常接近0，表示设备没有被移动。
		}
	}

}
