package item.finn.com.datacollection.protocal.element;

import item.finn.com.datacollection.protocal.Element;
import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

public class HeartBeatElement extends Element {

	public HeartBeatElement() {
	};

	@Override
	public int getCode() {
		return 0X1012;
	}

	@Override
	public int getReCode() {
		return 0X9012;
	}

	@Override
	public void toByteArray(StreamWrite write) {
	}

	@Override
	public void parse(StreamRead read) {

	}

}
