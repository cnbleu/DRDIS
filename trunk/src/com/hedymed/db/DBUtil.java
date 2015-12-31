package com.hedymed.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
	private static DBUtil mInstance;
	private SQLiteDatabase mSQLiteDatabase;

	private DBUtil(SQLiteDatabase database) {
		mSQLiteDatabase = database;
	}
	/**
	 * 初始化数据库操作DBUtil类
	 */
	public static DBUtil getInstance(SQLiteDatabase database) {
		if (mInstance == null) {
			mInstance = new DBUtil(database);
		}
		return mInstance;
	}
	/**
	 * 关闭数据库
	 */
	public void close() {
		mSQLiteDatabase.close();
		mSQLiteDatabase = null;
		mInstance = null;
	}

	/**
	 * 添加数据
	 */
	public void insertData(String table, ContentValues values) {
		mSQLiteDatabase.insert(table, null, values);
	}

	/**
	 * 更新数据
	 * 
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void updateData(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		mSQLiteDatabase.update(table, values, whereClause,
				whereArgs);
	}

	/**
	 * 删除数据
	 * 
	 * @param whereClause
	 * @param whereArgs
	 */
	public void deleteData(String table, String whereClause, String[] whereArgs) {
		mSQLiteDatabase
				.delete(table, whereClause, whereArgs);
	}

	/**
	 * 查询数据
	 * 
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor selectData(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Cursor cursor = mSQLiteDatabase.query(table,columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	
	public List<Map<String, String>> cursor2List(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int cols_len = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < cols_len; i++) {
				String cols_name = cursor.getColumnName(i);
				String cols_values = cursor.getString(i);
				if (cols_values == null) {
					cols_values = "";
				}
				map.put(cols_name, cols_values);
			}
			list.add(map);
		}
		return list;
	}
	
}