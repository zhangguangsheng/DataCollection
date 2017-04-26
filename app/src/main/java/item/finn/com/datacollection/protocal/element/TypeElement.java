package item.finn.com.datacollection.protocal.element;

import java.util.Arrays;
import java.util.List;

import android.util.Log;

import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.util.PrintUtil;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

public class TypeElement extends Element {

	private short _byte;
	private short[] _bytearray[];
	private int _word;
	private short _short = 255;
	private long _dword;
	private int _long = -1;
	private String _string;
	private String _datetime;

	public void toByteArray(StreamWrite write) {
		PrintUtil.printHexByteArray(write.toByteArray());
		write.writeByte((short) 255);// 255
		write.writeWord(256);// 1,0
		write.writeShort(_short);// 0, 255
		write.writeDWord(65536);// 0, 1, 0, 0
		write.writeLong(_long);// 255, 240, 0, 0
		write.writeString(_string);// 0
		write.writeBcdDateTime("20151010123456");// 20, 15, 10, 10, 12, 34,56
		PrintUtil.printHexByteArray(write.toByteArray());

	}

	@Override
	public int getCode() {
		return 0X1009;
	}

	@Override
	public int getReCode() {
		return 0X9009;
	}

	@Override
	public void parse(StreamRead read) {

	};
}
