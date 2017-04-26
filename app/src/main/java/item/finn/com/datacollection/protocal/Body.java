package item.finn.com.datacollection.protocal;

import java.util.ArrayList;
import java.util.List;

import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

import android.util.Log;

/**
 * 消息体封装
 * 
 * @author l
 * 
 */
public class Body {

	// 3.1.1 设备请求消息头(Y_REQUST_HEAD)
	protected String deviceid = "456654";
	protected String checkTime = "20160606123456";
	protected long tripId = 1;

	private List<Element> elements = new ArrayList<Element>();

	public List<Element> getElements() {
		return elements;
	}

	public void setRequestType(int requestType) {
		this.recode = requestType;
	}

	public void toByteArray(StreamWrite write) {
		write.writeWord(recode);
		write.writeString(deviceid);
		write.writeBcdDateTime(checkTime);
		write.writeDWord(tripId);
		for (Element e : elements) {
			e.toByteArray(write);
		}
	}

	/**************** 服务器端回复 **************************************/
	// 3.1.2 平台回应消息头(Y_RESPONSE_HEAD)
	protected int recode;
	protected int errcode;
	protected String errmsg;

	public int getRecode() {
		if (recode == 0 && elements.size() > 0) {
			recode = elements.get(0).getReCode();
		}
		return recode;
	}

	public int getErrcode() {
		return errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void parse(StreamRead read) throws Exception {
		Log.i("read", "Body-parse");
		recode = read.readWord();
		errcode = read.readWord();
		errmsg = read.readASCII();
		for (Element e : elements) {
			e.parse(read);
		}
	}

	@Override
	public String toString() {
		return "recode=" + recode + ", errcode=" + errcode + ", errmsg=" + errmsg + "\n" + elements.get(0).toString();
	}
}
