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


public class netRecvThread extends Thread {
	private static final short frameStartFlag = (short)0xF00A;
	
	private Socket recvSocket;
	Bitmap bitmap;
	DataInputStream dis;
	String picName;
	Context env;
	
	public netRecvThread(Context env, String threadName){
		super(threadName);
		this.env = env;
	}

	public void netRecvThreadStop() {
		interrupt();
	}
	
	public void run(){
		try {
			recvSocket = new Socket("192.168.37.52", 3000);
			dis = new DataInputStream(new BufferedInputStream(recvSocket.getInputStream()));
			try {
				while(!isInterrupted()){
					if(frameStartFlag == dis.readShort()){
						if(dis.readByte() == 3){
							int nameSize = dis.readInt();// 名称长度
							int imgSize = dis.readInt();// 文件长度
							byte[] buffer = new byte[nameSize];
							dis.readFully(buffer);
							picName = new String(buffer);
							File file = env.getDir("wang", Context.MODE_PRIVATE);
							String filePath = file.getCanonicalPath() + File.separator + picName;
							new File(filePath).deleteOnExit();//退出时删除该临时文件
							BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
			                buffer = new byte[2048];
			                int len = -1;
			                while (imgSize > 0 && (len = dis.read(buffer, 0, imgSize < buffer.length ? (int) imgSize : buffer.length)) != -1) {
			                    bos.write(buffer, 0, len);
			                    imgSize -= len;
			                }
			                bos.close();
			                //bitmap = BitmapFactory.decodeFile(picName);
			                
			                final BitmapFactory.Options options = new BitmapFactory.Options();
							options.inJustDecodeBounds = true; 
							BitmapFactory.decodeFile(filePath, options);
							//bis.reset();
							
							int width = env.getResources().getDisplayMetrics().widthPixels;
							int height = env.getResources().getDisplayMetrics().heightPixels;
							// Calculate inSampleSize
							options.inSampleSize = calculateSampleSize(options, width, height);
		
							// Decode bitmap with inSampleSize set
							options.inJustDecodeBounds = false; 
							bitmap = BitmapFactory.decodeFile(filePath, options);
							
							if(bitmap != null) {
								Intent intent = new Intent().setClassName(env, "com.hedymed.drdissys.disPictureActivity");
								intent.putExtra("netRecvBitmap", bitmap);
								env.startActivity(intent);
							}
							sleep(50);
						}
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
					if(dis != null)
						dis.close();
					if(recvSocket != null)
						recvSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
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
