package item.finn.com.datacollection;

public class GloableParams {
	/**
	 * 是否是wap方式联网
	 */
	public static boolean isWap = false;


	/**
	 * 心跳间隔
	 */
	public static int msgInterval = 1000 * 10; //
	// –任何情况下都不采集,0X01-时间间隔与距离间隔条件同时具备,0X02-时间间隔与距离间隔条件满足一项即可

	/**
	 * 一次发送记录数
	 */
	public static int sendRecordCount;
	/**
	 * 采集模式
	 */
	public static int mode; // 0X00

	/**
	 * 主机地地
	 * 
	 */
	public static String product_config_host = "123.57.244.92";
	public static int product_config_port = 9041;

	public static String develop_config_host = "123.57.244.92";
	public static int develop_config_port = 9301;

	// 除配置、升级外的所有数据发到此地址
	// public static String host = "10.1.19.73";
	public static String host;
	public static int port;

	public static int res_version;
	public static short res_init; // 是否清空本地保存数据（注:配置数据和行程ID不清空）
	public static String timestamp;// 服务器当前时间
	public static int runningInterval = 10;// 单位：秒
	public static int distanceInterval; // 单位：米

}
