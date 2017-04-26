package item.finn.com.datacollection.protocal.element;

import java.util.List;

import item.finn.com.datacollection.GloableParams;
import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

public class RunningDataElement extends Element {
	private int longitude;
	private int latitude;
	/**
	 * 方向：0-360度，正北为0度，顺时针顺序 星数：当前GPS定位星数 BIT0-BIT8：方向 BIT9-BIT14：表示星数
	 * BIT15:定位有效标识
	 */
	private int gpsinfo;//
	private short gpsspeed;
	private String gpstime;
	private int temstamp;
	private long mileagetamp;
	private short maxaccelerated;
	private int maxcentacceleration;
	private List<String> socketPacket;

	public RunningDataElement(List<String> socketPacket) {
		this.socketPacket = socketPacket;
	}

	public RunningDataElement() {

	}

	public void toByteArray(StreamWrite write) {
		write.writeByte((short) 1);
		write.writeWord(GloableParams.msgInterval);

		// String data = longitude + "," + latitude + "," + bearing + "," +
		// gpsSpeed + "," + sdf.format(new Date())+ "," + 0 + "," + 0 + "," +
		// maxaccelerated + "," + maxcentacceleration;
		for (String p : socketPacket) {
			String[] split = p.split(",");
			// write.writeLong((int) (Float.parseFloat(split[0]) * 1000000));
			// write.writeLong((int) (Float.parseFloat(split[1]) * 1000000));
			// write.writeWord((int) Float.parseFloat(split[2]));
			// write.writeByte((short) Float.parseFloat(split[3]));
			// write.writeBcdDateTime(split[4]);
			// write.writeWord((int) Float.parseFloat(split[5]));//
			// write.writeDWord((long) Double.parseDouble(split[6]));
			// write.writeShort((short) Float.parseFloat(split[7]));
			// write.writeWord((int) Float.parseFloat(split[8]));

			write.writeLong((int) (Float.parseFloat(split[0]) * 1000000));
			write.writeLong((int) (Float.parseFloat(split[1]) * 1000000));
			write.writeWord((int) Float.parseFloat(split[2]));
			write.writeByte((short) Float.parseFloat(split[3]));
			write.writeBcdDateTime(split[4]);
			write.writeDWord((long) Float.parseFloat(split[5]));//
			write.writeDWord((long) Double.parseDouble(split[6]));
			write.writeByte((short) 0);
			write.writeByte((short) 0);
			write.writeShort((short) Float.parseFloat(split[7]));
			write.writeWord((int) Float.parseFloat(split[8]));
			write.writeDWord((long) 0);
			write.writeByte((short) 0);
			write.writeByte((short) 2);
			write.writeWord((int) 0);
			write.writeDWord((long) 0);
			write.writeByteArray(new short[] { 1, 1, 1, 1, 1, 1 });

		}
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
