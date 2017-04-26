package item.finn.com.datacollection;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import item.finn.com.datacollection.protocal.Message;
import item.finn.com.datacollection.protocal.element.ConfigElement;
import item.finn.com.datacollection.service.MainService;
import item.finn.com.datacollection.util.PrintUtil;
import item.finn.com.datacollection.util.SocketClient;

public class CollectionInterface {
	private Message message;

	public void init(final Context context) {

		new Thread() {
			private Message message;
			private SharedPreferences sp;

			public void run() {
				try {
					// message = recv(ConfigElement.class);
					// startService(context);
					sp = context.getSharedPreferences("acq_config", Context.MODE_PRIVATE);
					SocketClient client = new SocketClient(GloableParams.product_config_host,
							GloableParams.product_config_port);
					byte[] byteArray = new Message(new ConfigElement()).getByteArray();
					if (client.send(byteArray)) {
						message = client.recv(ConfigElement.class);
						if (message != null) {
							ConfigElement element = (ConfigElement) message.getBody().getElements().get(0);
							GloableParams.host = element.getLogicAddressIp();
							GloableParams.port = element.getLogicAddressPort();
							GloableParams.msgInterval = element.getMsgInterval();
							GloableParams.res_init = element.getRes_init();

							sp.edit().putString("host", GloableParams.host).putInt("port", GloableParams.port)
									.putInt("msgInterval", GloableParams.msgInterval)
									.putInt("res_init", GloableParams.res_init).commit();

							Log.i("test", "yyyyyyyyy" + GloableParams.host);
							startService(context);
						}
					} else if (message == null) {
						GloableParams.host = sp.getString("host", GloableParams.host);
						GloableParams.port = sp.getInt("port", GloableParams.port);
						GloableParams.msgInterval = sp.getInt("msgInterval", GloableParams.msgInterval);
						GloableParams.res_init = (short) sp.getInt("res_init", GloableParams.res_init);
						// startService(context);
					}

				} catch (Exception e) {
					PrintUtil.printException2Error(e);
				}
			}
		}.start();

	}

	private void startService(Context context) {
		if (!isServiceWorked(context, "item.finn.com.datacollection.service.MainService")) {
			context.startService(new Intent(context, MainService.class));
		}
		// if (!isServiceWorked(context,
		// "item.finn.com.datacollection.service.SocketService")) {
		// context.startService(new Intent(context, SocketService.class));
		// }
	}

	// 启动服务
	public static boolean isServiceWorked(Context context, String serviceName) {
		ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(Integer.MAX_VALUE);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	// public Message recv(Class<? extends Element> clazz) {
	// try {
	// Element element = clazz.newInstance();
	//
	// byte[] datas = { -86, -69, 0, 103, -1, -104, 1, 0, 0, 1, 0, 0, -112, 1,
	// 0, 0, 0, 1, 0, 0, 32, 22, 6, 20,
	// 16, 88, 56, 0, 49, 49, 49, 49, 49, 49, 49, 0, 0, -1, 0, 49, 50, 51, 46,
	// 53, 55, 46, 50, 52, 52, 46,
	// 57, 50, 0, 39, 15, 49, 50, 51, 46, 53, 55, 46, 50, 52, 52, 46, 57, 50, 0,
	// 35, 81, 49, 50, 51, 46,
	// 53, 55, 46, 50, 52, 52, 46, 57, 50, 0, 36, -71, 0, 1, -1, -1, 2, 0, 5, 1,
	// 44, 0, 30, 2, -48, 4, -1,
	// -1, 115, 120, 0, 0, 0, 0 };
	// message = new Message(element).parse(new ByteArrayInputStream(datas));
	// Log.i("test", message.getBody().getElements().get(0).toString());
	//
	// // Message message = new Message(element).parse(is);
	// if (message.getHeader().getPacketLen() > 13) {
	// return message;
	// }
	// return null;
	// } catch (Exception e) {
	// // 状态清空
	// PrintUtil.printException2Error(e);
	// message.toString2();
	// return null;
	// }
	// }
}
