package item.finn.com.datacollection.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import android.util.Log;

public class PrintUtil {

	public static void printHexByteArray(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			Log.i("write", "bytes:" + bytes);
		} else {
			short[] temp = new short[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				temp[i] = (short) (b < 0 ? 256 + b : b);
			}
			Log.i("write", Arrays.toString(temp));
		}

	}

	public static void printException2Error(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("test", "\r\n" + sw.toString() + "\r\n");
		} catch (Exception e2) {
			throw new RuntimeException("打印异常失败");
		}
	}

}
