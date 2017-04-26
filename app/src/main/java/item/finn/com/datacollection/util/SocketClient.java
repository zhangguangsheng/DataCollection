package item.finn.com.datacollection.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.R.integer;
import android.util.Log;

import item.finn.com.datacollection.GloableParams;
import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.protocal.Message;
import item.finn.com.datacollection.service.SocketService;

public class SocketClient {
	private InputStream is;
	private OutputStream os;
	private Socket socket;

	public SocketClient(String host, int port) throws UnknownHostException, IOException {
		connect(host, port);
	}

	public boolean send(byte[] data) {
		try {
			if (data == null || data.length <= 0) {
				throw new RuntimeException();
			}
			os.write(data);
			Log.i("test", "request:0x" + Integer.toHexString(data[12]) + Integer.toHexString(data[13]) + "====>"
					+ BytesUtil.bytesToHexString(data));

			os.flush();
			return true;
		} catch (Exception e) {
			// 状态清空
			PrintUtil.printException2Error(e);
			disconnect();
			return false;
		}
	}

	public Message recv(Class<? extends Element> clazz) {
		try {
			Element element = clazz.newInstance();
			Message message = new Message(element).parse(is);
			if (message != null && message.getHeader().getPacketLen() > 13) {
				return message;
			}
			return null;
		} catch (Exception e) {
			// 状态清空
			PrintUtil.printException2Error(e);
			disconnect();
			return null;
		}
	}

	public void disconnect() {
		try {
			if (null != socket) {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
				socket = null;
			}
		} catch (IOException e) {
			PrintUtil.printException2Error(e);
		}
	}

	public void connect(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		socket.setSoTimeout(30 * 1000);// 如果超过30秒还没有数据，则抛出
		socket.setTcpNoDelay(true);

		is = socket.getInputStream();
		os = socket.getOutputStream();

		SocketService.linktestNum = 1;
		SocketService.isLogin = false;
	}

	public boolean isConnnect() {
		return socket != null && socket.isConnected();
	}
}
