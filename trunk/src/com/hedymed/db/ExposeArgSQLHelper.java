package com.hedymed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExposeArgSQLHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "database.db";// 数据库名称
	public static final int VERSION = 1;
	
	public static final String TABLE_CHANNEL = "channel";//数据表 

	public static final String ID = "id";
	public static final String KV = "kv";
	public static final String MA = "ma";
	public static final String MS = "ms";
	public static final String MAS = "mas";
	private Context context;
	public ExposeArgSQLHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
 
	public Context getContext(){
		return context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists "+TABLE_CHANNEL +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ID + " INTEGER , " +
				KV + " INTEGER , " +
				MA + " INTEGER , " +
				MS + " INTEGER , " +
				MAS + " REAL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
		onCreate(db);
	}

}
