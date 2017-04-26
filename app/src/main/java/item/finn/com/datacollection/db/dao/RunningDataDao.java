package item.finn.com.datacollection.db.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import item.finn.com.datacollection.db.DatabaseManager;
import item.finn.com.datacollection.db.RunningDataHelper;
import item.finn.com.datacollection.protocal.Message;
import item.finn.com.datacollection.protocal.element.RunningDataElement;

/**
 * 
 * 确保多线程中安全操作数据库，保证只有一个数据库连接被占用
 *
 */
public class RunningDataDao {

	private DatabaseManager manager;
	private static RunningDataDao instance;

	/**
	 * 缓存N条未发送的数据包
	 */
	private static List<byte[]> queueCache = new LinkedList<byte[]>();
	private Integer offerMaxPacket = 3; // 缓存容器最多存三个数据包

	/**
	 * 采集M次的消息队列
	 */
	private static List<String> socketPacket = new LinkedList<String>();
	private Integer maxRecord = 10; // 每个数据包存10条记录

	public static void initRunningDataDao(Context context) {
		if (instance == null) {
			instance = new RunningDataDao(context);
		}
	}

	public static RunningDataDao getInstance() {
		if (instance == null) {
			throw new IllegalStateException(DatabaseManager.class.getSimpleName() + " 没有初始化,先调用initRunningDataDao(..)");
		}
		return instance;
	}

	private RunningDataDao(Context context) {
		DatabaseManager.initializeInstance(new RunningDataHelper(context));
		manager = DatabaseManager.getInstance();
	}

	/**
	 * 采集到的数据添加到SocketPacket队列
	 * 
	 * @param data
	 */
	public void addToSocketPacket(String data) {
		synchronized (RunningDataDao.class) {
			Integer size = socketPacket.size();
			if (size == maxRecord) {// 消息队列满10条 打包
				Message message = new Message(new RunningDataElement(socketPacket));
				if (queueCache.size() == offerMaxPacket) {// 如果queueCache容器超过3条,数据入库,否则add到缓存容器
					insertDB(message.getByteArray());
				} else {
					queueCache.add(message.getByteArray());
				}
				socketPacket.clear();
			}
			socketPacket.add(data);

		}
	}

	/**
	 * 获取一个封装好的数据包,如果当前容器不足3条,查表填充
	 * 
	 * @return
	 */
	public byte[] queryCache() {
		synchronized (RunningDataDao.class) {
			byte[] data = null;
			if (queueCache != null && queueCache.size() > 0) {
				data = queueCache.remove(0);
			}
			int count = offerMaxPacket - queueCache.size();
			queueCache.addAll(query(count));
			delete(count);
			return data;
		}
	}

	public void insertDB(byte[] data) {
		SQLiteDatabase db = manager.openDatabase();
		ContentValues values = new ContentValues();
		values.put("rundata", data);
		db.insert("rundata", null, values);
	}

	/**
	 * 一次查询N条
	 * 
	 * @param count
	 * @return
	 */
	public List<byte[]> query(int count) {

		List<byte[]> result = new ArrayList<byte[]>();
		SQLiteDatabase db = manager.openDatabase();
		Cursor cursor = db.query("rundata", new String[] { "rundata" }, "_id limit " + count, null, null, null, null);
		while (cursor.moveToNext()) {
			result.add(cursor.getBlob(0));
		}
		return result;
	}

	/**
	 * 一次删除前N条
	 * 
	 * @param count
	 * @return
	 */
	public void delete(int count) {
		SQLiteDatabase db = manager.openDatabase();
		db.execSQL("delete from rundata where _id in (select _id from rundata   limit 0, " + count + ")");
	}
}
