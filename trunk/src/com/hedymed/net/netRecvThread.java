package com.hedymed.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hedymed.drdissys.MainActivity;
import com.hedymed.engineer.preferenceInterface;

public class netRecvThread extends Thread implements preferenceInterface.localIPChangelistener{
	private static final short frameStartFlag = (short)0xF00A;
	
	private Socket mRecvSocket;
	private Bitmap mBitmap;
	private DataInputStream mDis;
	private String mPicName;
	private String mPCAddr;
	private Context mContext;
	private boolean mIPChangeFlag;
	
	public netRecvThread(Context env, String threadName){
		super(threadName);
		mContext = env;
		mIPChangeFlag = true;
		mPCAddr = MainActivity.mPreferences.getString("localIP", null);
				
		if(env instanceof MainActivity)
			((MainActivity)env).setLocalIPChangeListener(this);
		
	}

	public void netRecvThreadStop() {
		interrupt();
	}
	
	public void getChangedIP(String key) {
		mPCAddr = MainActivity.readPreferencesString(key);
		mIPChangeFlag = true;
	}
	
	public void run(){
		while(!isInterrupted()){
			try {
				if(mIPChangeFlag) {
					mIPChangeFlag = false;
					if(mRecvSocket != null)
						mRecvSocket.close();
					
					mRecvSocket = new Socket(mPCAddr, 3000);
					mDis = new DataInputStream(new BufferedInputStream(mRecvSocket.getInputStream()));
				}
				
				if(mDis != null && frameStartFlag == mDis.readShort()){
					if(mDis.readByte() == 3){
						int nameSize = mDis.readInt();// 名称长度
						int imgSize = mDis.readInt();// 文件长度
						byte[] buffer = new byte[nameSize];
						mDis.readFully(buffer);
						mPicName = new String(buffer);
						File file = mContext.getDir("wang", Context.MODE_PRIVATE);
						String filePath = file.getCanonicalPath() + File.separator + mPicName;
						new File(filePath).deleteOnExit();//退出时删除该临时文件
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		                buffer = new byte[2048];
		                int len = -1;
		                while (imgSize > 0 && (len = mDis.read(buffer, 0, imgSize < buffer.length ? (int) imgSize : buffer.length)) != -1) {
		                    bos.write(buffer, 0, len);
		                    imgSize -= len;
		                }
		                bos.close();
		                //bitmap = BitmapFactory.decodeFile(picName);
		                
		                final BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true; 
						BitmapFactory.decodeFile(filePath, options);
						//bis.reset();
						
						int width = mContext.getResources().getDisplayMetrics().widthPixels;
						int height = mContext.getResources().getDisplayMetrics().heightPixels;
						// Calculate inSampleSize
						options.inSampleSize = calculateSampleSize(options, width, height);
	
						// Decode bitmap with inSampleSize set
						options.inJustDecodeBounds = false; 
						mBitmap = BitmapFactory.decodeFile(filePath, options);
						
						if(mBitmap != null) {
							Intent intent = new Intent().setClassName(mContext, "com.hedymed.drdissys.disPictureActivity");
							intent.putExtra("netRecvBitmap", mBitmap);
							mContext.startActivity(intent);
						}
						sleep(50);
					}
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch (UnknownHostException e) 
			{	
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(mDis != null)
						mDis.close();
					if(mRecvSocket != null)
						mRecvSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    public static int calculateSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {

        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
    
}
