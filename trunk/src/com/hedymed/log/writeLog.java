package com.hedymed.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class writeLog implements writeLoglistener {
	private Context mContext;
	private File mLogFile, mErrorFile;
	static private PrintWriter mOperatorPrintWriter;
	static private PrintWriter mErrorPrintWriter;
	
	public writeLog(Context context) {
		mContext = context;
		Log.i("in operatorLog construct function",  "start service");
		Intent intent = new Intent();
		intent.setAction("com.hedymed.drdissys.LOG_SERVICE");
		context.startService(intent);
		File dataFile = mContext.getDir("log", Context.MODE_PRIVATE);
		try {
			mLogFile = new File(dataFile.getCanonicalPath(), "operator.log");
			mOperatorPrintWriter = new PrintWriter(new FileWriter(mLogFile, true), true);
			
			mErrorFile = new File(dataFile.getCanonicalPath(), "error.log");
			mErrorPrintWriter = new PrintWriter(new FileWriter(mErrorFile, true), true);
		} catch (IOException e) {
			Log.i("in operatorLog construct function",  e.getMessage());
		} 
	}
	
	public void writeOPeratorLog(String str) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		String dateStr = dateFormat.format(new Date());
		dateStr += "    " + str;
		mOperatorPrintWriter.println(dateStr);
	}
	
	public void writErrorLog(String str) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		String dateStr = dateFormat.format(new Date());
		dateStr += "    " + str;
		mErrorPrintWriter.println(dateStr);
	}
	
	
	public void close() {
		if(mOperatorPrintWriter != null)
			mOperatorPrintWriter.close();
		
		if(mErrorPrintWriter != null)
			mErrorPrintWriter.close();
	}
	
}
