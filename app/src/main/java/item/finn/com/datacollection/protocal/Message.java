package item.finn.com.datacollection.protocal;

import java.io.InputStream;

import android.util.Log;

import item.finn.com.datacollection.util.BytesUtil;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

/**
 * 协议封装
 * 
 */
public class Message {
	private Header header = new Header();
	private Body body = new Body();
	private short checkDigit;

	public Message(Element element) {
		getBody().getElements().add(element);

	}

	public Header getHeader() {
		return header;
	}

	public Body getBody() {
		return body;
	}

	/**
	 * 协议封装
	 * 
	 * @param serializer
	 */
	public byte[] getByteArray() {
		Element element = body.getElements().get(0);
		if (element == null) {
			throw new IllegalArgumentException("element != null");
		}

		StreamWrite write = new StreamWrite();
		body.setRequestType(element.getCode());
		header.toByteArray(write);
		body.toByteArray(write);
		write.writeByte((short) 0);// 校验码
		byte[] data = write.toByteArray();
		// 添加协议包长度
		int length = data.length - 2;
		data[3] = (byte) length;
		data[2] = (byte) (length >>> 8);
		// 添加长度校验
		data[5] = (byte) ~length;
		data[4] = (byte) (~length >>> 8);
		// 添加数据包的校验码
		checkDigit = data[6];
		for (int i = 7; i < length - 1; i++) {
			checkDigit = (byte) (checkDigit ^ data[i]);
		}
		data[data.length - 1] = (byte) checkDigit;
		return data;
	}

	public Message parse(InputStream is) throws Exception {
		StreamRead read = new StreamRead(is);
		int readWord = read.readWord();
		if (readWord != 0xAABB) {
			Log.i("test", "response:" + body.getRecode() + "<====" + readWord);
			return null;
		}
		header.parse(read);
		body.parse(read);
		checkDigit = read.readByte();
		byte[] bytes = read.getBytes();
		Log.i("test",
				"response:0x" + Integer.toHexString(body.getRecode()) + "<====" + BytesUtil.bytesToHexString(bytes) + "\n" + header.toString()
						+ body.toString());
		return this;
	}

	// public void toString2() {
	// byte[] bytes = read.getBytes();
	// Log.i("test", "response:<====    " +
	// BytesUtil.bytesToHexString(bytes));
	// Log.i("test", "response:<====    " + Arrays.toString(bytes));
	// Log.i("test", "response:<====    " + header.toString() +
	// body.toString());
	// }
}
