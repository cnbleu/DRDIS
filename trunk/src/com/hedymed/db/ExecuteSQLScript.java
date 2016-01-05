package com.hedymed.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class ExecuteSQLScript {
	public static final String TAG = "executeSQLScript";
	
	public static void executeScript(Context context, SQLiteDatabase SQLiteDatabase, String scriptFileNme) {
		try {
		     InputStream in = context.getAssets().open(scriptFileNme);
		     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		     String sqlUpdate = null;
		     while ((sqlUpdate = bufferedReader.readLine()) != null) {
		           if (!TextUtils.isEmpty(sqlUpdate)) {
		        	   SQLiteDatabase.execSQL(sqlUpdate);
		           }
		     }
		     bufferedReader.close();
		     in.close();
		} catch (SQLException e) {
		        Log.i(TAG, e.getMessage());
		} catch (IOException e) {
		        Log.i(TAG, e.getMessage());
		}
	}
	
}
