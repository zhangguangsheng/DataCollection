package item.finn.com.datacollection.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RunningDataHelper extends SQLiteOpenHelper {

	public RunningDataHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public RunningDataHelper(Context context) {
		super(context, "rundata.db", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table rundata(_id integer primary key autoincrement,rundata blob)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
