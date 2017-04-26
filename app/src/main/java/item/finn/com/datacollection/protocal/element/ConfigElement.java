package item.finn.com.datacollection.protocal.element;

import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.util.PrintUtil;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

@SuppressWarnings("unused")
public class ConfigElement extends Element {
	private String req_deviceid;// <=20设备的唯一编号，需要在平台进行授权
	private String req_deviceCheckCode; // <=20
	private short req_deviceType = 4;// 标明设备的类型，0-obd，1-车机，2-TBOX，3-智能手机
	private String req_hardwareVersion = "1.0";
	private String req_firmwareVersion = "1.0";
	private String req_softwareVersion = "1.0";
	/******* temp **********/
	private String vin;
	private String vid;
	private short dianosisType;
	private String devicesrc;

	private String sweptvolume;// 排量
	private int year;
	private String svraddr_agps_name;// 除配置、升级外的所有数据发到此地址
	private int svraddr_agps_port;//
	private int waitforsleep;//
	private int sleepreportspace;//
	private short voltagereportspace;//
	private int shutdowntime;//

	public ConfigElement() {

	}

	@Override
	public void toByteArray(StreamWrite write) {
		// writeRequestHeader(write);
		write.writeString(req_deviceCheckCode);
		write.writeByte(req_deviceType);
		write.writeString(devicesrc);
		write.writeString(vin);
		write.writeByte(dianosisType);

		// write.writeString(req_deviceid);
		write.writeString(req_hardwareVersion);
		write.writeString(req_firmwareVersion);
		write.writeString(req_softwareVersion);
	}

	/**
	 * ************************* 响应**********************
	 */

	@Override
	public void parse(StreamRead read) {
		try {
			lastversion = read.readWord();// 20->32
			initnum = read.readByte();// 16->22
			servertime = read.ReadBcdDateTime();
			vin = read.readASCII();
			vid = read.readASCII();
			year = read.readWord();
			sweptvolume = read.readASCII();
			svraddr_update_name = read.readASCII();
			svraddr_update_port = read.readWord();
			svraddr_agps_name = read.readASCII();
			svraddr_agps_port = read.readWord();
			svraddr_business_name = read.readASCII();
			svraddr_business_port = read.readWord();
			timespace = read.readWord();
			distancespace = read.readWord();
			mode = read.readByte();
			packagenum = read.readWord();
			waitforsleep = read.readWord();
			linktestspace = read.readWord();
			sleepreportspace = read.readWord();
			voltagereportspace = read.readByte();
			shutdowntime = read.readWord();

		} catch (Exception e) {
			PrintUtil.printException2Error(e);
		}
	}

	private int lastversion;
	private short initnum; // 是否清空本地保存数据（注:配置数据和行程ID不清空）
	private String servertime;// 服务器当前时间
	private String svraddr_update_name;
	private int svraddr_update_port;
	private String svraddr_business_name;// 除配置、升级外的所有数据发到此地址
	private int svraddr_business_port;//

	private int timespace;// 单位：秒
	private int distancespace; // 单位：米
	private short mode; // 0X00
						// –任何情况下都不采集,0X01-时间间隔与距离间隔条件同时具备,0X02-时间间隔与距离间隔条件满足一项即可
	private int packagenum;
	private int linktestspace;

	public short getRes_init() {
		return initnum;
	}

	public int getMsgInterval() {
		return packagenum;
	}

	public String getLogicAddressIp() {
		return svraddr_business_name;
	}

	public int getLogicAddressPort() {
		return svraddr_business_port;
	}

	public int getHeartCount() {
		return packagenum;
	}

	@Override
	public int getCode() {
		return 0X1001;
	}

	@Override
	public int getReCode() {
		return 0X9001;
	}

	@Override
	public String toString() {
		return "ConfigElement [vin=" + vin + ", vid=" + vid + ", dianosisType=" + dianosisType + ", devicesrc="
				+ devicesrc + ", sweptvolume=" + sweptvolume + ", year=" + year + ", svraddr_agps_name="
				+ svraddr_agps_name + ", svraddr_agps_port=" + svraddr_agps_port + ", waitforsleep=" + waitforsleep
				+ ", sleepreportspace=" + sleepreportspace + ", voltagereportspace=" + voltagereportspace
				+ ", shutdowntime=" + shutdowntime + ", lastversion=" + lastversion + ", initnum=" + initnum
				+ ", servertime=" + servertime + ", svraddr_update_name=" + svraddr_update_name
				+ ", svraddr_update_port=" + svraddr_update_port + ", svraddr_business_name=" + svraddr_business_name
				+ ", svraddr_business_port=" + svraddr_business_port + ", timespace=" + timespace + ", distancespace="
				+ distancespace + ", mode=" + mode + ", packagenum=" + packagenum + ", linktestspace=" + linktestspace
				+ "]";
	}

}
