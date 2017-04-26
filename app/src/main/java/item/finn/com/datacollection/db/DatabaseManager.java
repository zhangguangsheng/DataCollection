package item.finn.com.datacollection.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

import item.finn.com.datacollection.db.dao.RunningDataDao;

/**
 * 负责管理单个 SQLiteOpenHelper 对象的单例 DatabaseManager,保证只有一个数据库连接被占用
 * 
 *
 */

public class DatabaseManager {

	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static DatabaseManager instance;
	private static SQLiteOpenHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public static void initializeInstance(SQLiteOpenHelper helper) {
		synchronized (RunningDataDao.class) {

			if (instance == null) {
				instance = new DatabaseManager();
				mDatabaseHelper = helper;
			}
		}
	}

	public static DatabaseManager getInstance() {
		synchronized (RunningDataDao.class) {
			if (instance == null) {
				throw new IllegalStateException(DatabaseManager.class.getSimpleName()
						+ " 没有初始化,先调用initializeInstance(..)");
			}
			return instance;
		}

	}

	/**
	 * 多线程同时调用 SQLiteDatabase 对象实例会报IllegalStateException
	 * ,所以需要确保数据库没有被占用的情况下，才去关闭它 如果不关闭会报Leak foundCaused by:
	 * java.lang.IllegalStateException: SQLiteDatabase created and never closed
	 * 
	 * @return
	 */
	public SQLiteDatabase openDatabase() {
		synchronized (RunningDataDao.class) {

			if (mOpenCounter.incrementAndGet() == 1) {// 内置一个标志数据库被打开多少次的计数器。如果计数为1,代表需要打开一个新的数据库连接，否则，数据库连接已经存在
				mDatabase = mDatabaseHelper.getWritableDatabase();
			}
			return mDatabase;
		}
	}

	public void closeDatabase() {
		synchronized (RunningDataDao.class) {

			if (mOpenCounter.decrementAndGet() == 0) {
				// Closing database
				mDatabase.close();
			}
		}
	}
}