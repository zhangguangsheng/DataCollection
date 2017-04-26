package item.finn.com.datacollection.protocal.element;

import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

public class LoginElement extends Element {
	private String deviceId;
	private String deviceCode;
	private short deviceType = 1;
	private String deviceSrc;
	private String deviceVin;
	private short diagnoseType = 0xff;

	private String hardwareVersion = "1.0";
	private String firmwareVersion = "1.0";
	private String softwareVersion = "1.0";

	public LoginElement() {

	}

	public LoginElement(String deviceId, short deviceType, String deviceCode_b, String deviceSrc, String deviceVin,
			short diagnoseType_b, String hardwareVersion, String firmwareVersion, String softwareVersion) {
		super();

		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.deviceCode = deviceCode_b;
		this.deviceSrc = deviceSrc;
		this.deviceVin = deviceVin;
		this.diagnoseType = diagnoseType_b;
		this.hardwareVersion = hardwareVersion;
		this.firmwareVersion = firmwareVersion;
		this.softwareVersion = softwareVersion;
	}

	@Override
	public void toByteArray(StreamWrite write) {
		write.writeString("");
		write.writeByte(deviceType);
		write.writeString(deviceSrc);
		write.writeString(deviceVin);
		write.writeByte(diagnoseType);

		// write.writeString(deviceCode);
		// write.writeString(deviceId);
		write.writeString(hardwareVersion);
		write.writeString(firmwareVersion);
		write.writeString(softwareVersion);
	}

	@Override
	public void parse(StreamRead read) {

	}

	@Override
	public int getCode() {
		return 0X1003;
	}

	@Override
	public int getReCode() {
		return 0X9003;
	}

}
