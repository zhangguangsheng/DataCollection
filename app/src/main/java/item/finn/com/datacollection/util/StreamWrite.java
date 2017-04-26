package item.finn.com.datacollection.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.text.TextUtils;
import android.util.Log;

/**
 * 数据流转换类
 *
 */
public class StreamWrite {
	private ByteArrayOutputStream baos;

	public StreamWrite() {
		baos = new ByteArrayOutputStream();
	}

	public void writeByte(short num) {
		baos.write((byte) num);
	}

	public void writeByteArray(short[] nums) {
		for (int i = 0; i < nums.length; i++) {
			baos.write((byte) nums[i]);
		}
	}

	public void writeWord(int num) {

		byte[] buf = new byte[2];
		for (int i = 0; i < buf.length; i++) {
			buf[1 - i] = (byte) (num >>> (8 * i));
		}
		baos.write(buf, 0, 2);
	}

	public void writeDWord(long num) {
		byte[] buf = new byte[4];
		for (int i = 0; i < buf.length; i++) {
			buf[3 - i] = (byte) (num >>> (8 * i));
		}
		baos.write(buf, 0, 4);
	}

	public void writeLong(int num) {
		writeDWord(num);
	}

	public void writeShort(short num) {
		byte[] buf = new byte[2];
		for (int i = 0; i < buf.length; i++) {
			buf[1 - i] = (byte) (num >>> (8 * i));
		}
		baos.write(buf, 0, 2);
	}

	// public void writeString() {
	//
	// byte[] buf = new byte[2];
	// baos.write(buf, 0, 2);
	// }

	public void writeString(String str) {
		writeString(str, '\0');
	}

	public void writeString(String str, char bspace) {
		if (TextUtils.isEmpty(str)) {
			str = "";
		}
		byte[] temp = str.getBytes();

		byte[] buf = new byte[temp.length + 1];
		for (int i = 0; i < temp.length; i++) {
			buf[i] = temp[i];
			// buf[i] = (byte) Integer.parseInt(Integer.toHexString(temp[i]));
		}

		buf[buf.length - 1] = (byte) bspace;
		baos.write(buf, 0, buf.length);
	}

	public void writeBcdDateTime(String bcdDatetime) {
		if (TextUtils.isEmpty(bcdDatetime)) {
			bcdDatetime = "00000000000000";
		}

		char[] chars = bcdDatetime.toCharArray();
		byte[] buf = new byte[chars.length / 2];
		for (int i = 0; i < buf.length; i++) {
			String s = chars[i * 2] + "" + chars[i * 2 + 1];
			buf[i] = Byte.parseByte(s, 16);
		}
		baos.write(buf, 0, buf.length);
	}

	public byte[] toByteArray() {
		if (baos != null && baos.size() > 0) {
			byte[] datas = baos.toByteArray();
			try {
				baos.close();
			} catch (IOException e) {
				PrintUtil.printException2Error(e);
			}
			return datas;
		}
		return null;
	}
}
