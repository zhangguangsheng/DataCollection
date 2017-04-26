package item.finn.com.datacollection.protocal;

import item.finn.com.datacollection.util.StreamRead;
import item.finn.com.datacollection.util.StreamWrite;

/**
 * 请求的公共部分
 * 
 * @author l
 *
 */
public abstract class Element {

	public abstract int getCode();

	public abstract int getReCode();

	public abstract void toByteArray(StreamWrite write);

	public abstract void parse(StreamRead read);

	// protected void writeRequestHeader(StreamWrite write) {
	// write.writeWord(0x1001);
	// write.writeString("");
	// write.writeBcdDateTime("20160610123456");
	// write.writeDWord(1);
	// }

}
