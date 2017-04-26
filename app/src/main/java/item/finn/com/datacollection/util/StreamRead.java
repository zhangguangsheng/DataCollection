package item.finn.com.datacollection.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.util.Log;

/**
 * 
 * 数据流解析类
 */
public class StreamRead {
	private InputStream is;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public StreamRead(InputStream is) {
		this.is = is;

	}

	public byte[] getBytes() {
		byte[] byteArray = baos.toByteArray();
		baos.reset();
		return byteArray;
	}

	public short readByte() throws Exception {
		byte[] buf = new byte[1];
		try {
			is.read(buf, 0, 1);
			baos.write(buf);
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
		return Short.parseShort(BytesUtil.bytesToHexString(buf[0]), 16);
		// (short) (buf[0] & 0xff);
	}

	public short[] readByteArray(int n) throws Exception {
		byte[] temp = new byte[n];
		short[] buf = new short[n];
		try {
			is.read(temp, 0, n);
			baos.write(temp);
			for (int i = 0; i < temp.length; i++) {
				buf[i] = (short) (temp[0] & 0xff);
			}
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}

		return buf;
	}

	public int readWord() throws Exception {
		try {
			byte[] buf = new byte[2];
			is.read(buf, 0, 2);
			baos.write(buf);
			return Integer.parseInt(BytesUtil.bytesToHexString(buf), 16);

		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
	}

	public Long readDWord() throws Exception {
		try {
			byte[] buf = new byte[4];
			is.read(buf, 0, 4);
			baos.write(buf);
			return Long.parseLong(BytesUtil.bytesToHexString(buf), 16);
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
	}

	public Long readLong() throws Exception {
		try {
			byte[] buf = new byte[4];
			is.read(buf, 0, 4);
			baos.write(buf);
			return Long.parseLong(BytesUtil.bytesToHexString(buf), 16);
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
	}

	public Short readShort() throws Exception {
		try {
			byte[] buf = new byte[2];
			is.read(buf, 0, 2);
			baos.write(buf);
			return Short.parseShort(BytesUtil.bytesToHexString(buf), 16);
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
	}

	// public String readString() throws Exception {
	// return readString('\0');
	// }

	public String readString(char bspace) throws Exception {
		byte[] buf = new byte[1];
		StringBuffer sb = new StringBuffer();
		int len = 0;
		while (true) {
			if (is.read(buf, 0, 1) == 0) {
				baos.write(buf);
				if (len <= 0)
					throw (new Exception("stream is end!"));
				else {
					return "";
				}
			}
			if (buf[0] == bspace)
				break;
			sb.append(buf[0]);
			len++;
		}
		return sb.toString();
	}

	public String readASCII() throws Exception {
		byte[] buf = new byte[1];
		StringBuffer sb = new StringBuffer();
		int len = 0;
		while (true) {
			if (is.read(buf, 0, 1) == 0) {
				baos.write(buf);
				if (len <= 0)
					throw (new Exception("stream is end!"));
				else {
					return "";
				}
			}
			if (buf[0] == '\0')
				break;
			sb.append((char) buf[0]);
			len++;
		}
		return sb.toString();
	}

	public String ReadBcdDateTime() throws Exception {
		byte[] buf = new byte[7];
		StringBuffer sb = new StringBuffer();
		try {
			is.read(buf, 0, 7);
			baos.write(buf);
			return BytesUtil.bytesToHexString(buf);
			// for (int i = 0; i < buf.length; i++) {
			// sb.append((byte) ((buf[i] & 0xf0) >>> 4));
			// sb.append((byte) (buf[i] & 0x0f));
			// }
		} catch (IOException e) {
			throw (new Exception("stream is end!"));
		}
		// return sb.toString();

	}
}
