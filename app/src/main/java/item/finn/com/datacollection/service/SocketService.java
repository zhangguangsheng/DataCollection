package item.finn.com.datacollection.service;

import java.io.IOException;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import item.finn.com.datacollection.db.dao.RunningDataDao;
import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.protocal.Message;
import item.finn.com.datacollection.protocal.element.HeartBeatElement;
import item.finn.com.datacollection.protocal.element.LoginElement;
import item.finn.com.datacollection.protocal.element.RunningDataElement;
import item.finn.com.datacollection.util.NetUtil;
import item.finn.com.datacollection.util.PrintUtil;
import item.finn.com.datacollection.util.SocketClient;

public class SocketService {
	private Context context;

	private long SEND_TIME_HEART = 30 * 1000; // 发送心跳间隔
	private long SEND_TIME_RECREATE = 90 * 1000; // 重新创建连接间隔

	private boolean done; // 是否继续下轮循环标识
	private byte[] data; // 一个socket数据包
	private long lastRecvTime; // 上一次的接收成功的时间

	private SocketClient client;

	private String host;
	private int port;

	/**
	 * 登录状态标记
	 */
	public static boolean isLogin = false;
	public static int linktestNum = 1;

	public SocketService(Context context) throws UnknownHostException, IOException, InterruptedException {
		this.context = context;
	}

	public void init() throws UnknownHostException, IOException, InterruptedException {
		done = true;
		if (client == null) {
			SharedPreferences sp = context.getApplicationContext().getSharedPreferences("acq_config",
					Context.MODE_PRIVATE);
			host = sp.getString("host", "");
			port = sp.getInt("port", 0);
			client = new SocketClient(host, port);

		}
		roundScheduling();
	}

	/**
	 * 轮循上传任务
	 * 
	 * @throws UnknownHostException
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void roundScheduling() throws UnknownHostException, IOException, InterruptedException {
		while (done) {
			try {
				long currentTimeMillis = System.currentTimeMillis() - lastRecvTime;
				if (!NetUtil.checkNet(context)) {// 无网络
					client.disconnect();
					Log.i("test", "无网络");
					break;
				}
				if (!client.isConnnect()) {// 连接断开
					client.disconnect();
					client.connect(host, port);
					lastRecvTime = System.currentTimeMillis();
					Log.i("test", "连接断开");
				}
				if (!isLogin) {// 未登录
					isLogin = getResult(client, new Message(new LoginElement()).getByteArray(), LoginElement.class);
					if (!isLogin)
						Log.i("test", "未登录");
					continue;
				}

				if (currentTimeMillis >= SEND_TIME_RECREATE) { // 超90s,重新创建连接
					client.connect(host, port);
					Log.i("test", "超90s,重新创建连接");
					continue;
				} else if (currentTimeMillis >= SEND_TIME_HEART * linktestNum++) {// 超30s,发送一次心跳
					getResult(client, new Message(new HeartBeatElement()).getByteArray(), HeartBeatElement.class);
					Log.i("test", " 超30s,发送一次心跳");
				} else { // 发送运行数据
					if (data == null) {
						RunningDataDao.initRunningDataDao(context);
						data = RunningDataDao.getInstance().queryCache();
					}
					while (null != data && data.length > 0) {// 如果缓存队列不为空继续上传
						if (getResult(client, data, RunningDataElement.class)) {
							data = RunningDataDao.getInstance().queryCache();
							Log.i("test", "缓存队列不为空继续上传...");
							Thread.sleep(500);
							continue;
						} else {
							Log.i("test", "库为空");
							break;
						}
					}
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				Thread.sleep(1000 * 90);// ConnectException 90s后重试
				client.disconnect();
				PrintUtil.printException2Error(e);
			}
		}
	}

	/**
	 * 发送消息,返回消息是否发送成功
	 * 
	 * @param data
	 * @param clazz
	 * @return
	 */
	public boolean getResult(SocketClient client, byte[] data, Class<? extends Element> clazz) {
		if (client.send(data)) {
			Message message = client.recv(clazz);
			if (message != null && message.getBody().getErrcode() == 0) {
				lastRecvTime = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}
}
