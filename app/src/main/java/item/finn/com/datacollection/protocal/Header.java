package item.finn.com.datacollection.protocal;

import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

/**
 * 头信息封装
 * 
 * @author l
 *
 */
public class Header {

	/** 2.4.1 协议头 */
	private int packheader = 0xAABB;
	private int packetLen;
	private int rePacketLen;
	private int versionCode = 0x0100;
	private static long seriaCode = 1;// 流水号
	private int extra;// 保留字

	public Header() {

	}

	public Header(int packetLen, int versionCode, int extra) {
		super();
		this.packetLen = packetLen;
		this.versionCode = versionCode;
		this.extra = extra;
	}

	public static long getSeriaCode() {
		if (seriaCode > 0xFFFFFFFF)
			seriaCode = 1;
		return seriaCode++;
	}

	/**
	 * 序列化头节点
	 * 
	 * @param write
	 * 
	 * @param bytes
	 * 
	 * @param serializer
	 * @param wholeBody
	 */
	public void toByteArray(StreamWrite write) {
		write.writeWord(packheader);
		write.writeWord(packetLen);
		write.writeWord(~packetLen);
		write.writeWord(versionCode);
		// write.writeDWord(getSeriaCode());
		write.writeWord((int) getSeriaCode());
		write.writeWord(extra);
	}

	/***********************
	 * 服务器端回复
	 * 
	 * @param read
	 * @throws Exception
	 ***************************************************/

	public void parse(StreamRead read) throws Exception {
		packetLen = read.readWord();
		rePacketLen = read.readWord();
		versionCode = read.readWord();
		seriaCode = read.readWord();
		extra = read.readWord();

	}

	@Override
	public String toString() {
		return "packetLen=" + packetLen + ", rePacketLen=" + rePacketLen + ", versionCode=" + versionCode
				+ "seriaCode=" + seriaCode + ", extra=" + extra;
	}

	public int getPacketLen() {
		return packetLen;
	}
}
