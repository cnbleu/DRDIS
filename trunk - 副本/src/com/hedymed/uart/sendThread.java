package com.hedymed.uart;

import static com.hedymed.uart.uartUtils.CMD_CRC16_LENGTH;
import static com.hedymed.uart.uartUtils.CMD_MAX_LENGTH;
import static com.hedymed.uart.uartUtils.CMD_NAK_CHAR;
import static com.hedymed.uart.uartUtils.CMD_WRAP_LENGTH;
import static com.hedymed.uart.uartUtils.UART_SEND_DATA_KEY;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

enum enumMutex {
	MUTEX_UNLOCK, MUTEX_LOCK;
}

enum enumSendStatus {
	SEND_FAILURE, SEND_SUCCESS;
}

public class sendThread extends Thread {
	public Handler mSendHandler;
	private uartUtils mUartUtils;
	private StringBuilder mCmdBuilder;
	private enumMutex mSendMutex;
	private long mSendJiffy;
	private String mLastSendCMD;
	
	public sendThread(uartUtils uart, String threadName) {
		super(threadName);
		mUartUtils = uart;
		mCmdBuilder = new StringBuilder();
		mSendMutex = enumMutex.MUTEX_UNLOCK;
	}
	
	@Override
	public void run() {
		Looper.prepare();
		mSendHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String cmdData = (String)msg.getData().getCharSequence(UART_SEND_DATA_KEY);
				
				if(cmdData == null) {
					if(msg.what == uartUtils.NEXT_FRAME_FLAG)
						uartSendCmd();
					else if(msg.what == uartUtils.RESEND_FLAG)
						uartResendCmd();
				} else if(cmdData.equals(String.valueOf(CMD_NAK_CHAR)))
					uartHardwareSend(new byte[]{0x15});
				else if(msg.what == uartUtils.ACK_FLAG) 
					uartHardwareSend(getAsciiCrc(cmdData + uartUtils.CMD_ETX_CHAR));
				else if(msg.what == uartUtils.NEW_CMD_FLAG) {
					uartBuildCmd(cmdData);
					uartSendCmd();
				}
			}
		};
		Looper.loop();
	}

	public void stopItself() {
		mSendHandler.getLooper().quit();
	}
	
	public StringBuilder getCmdBuilder() {
		return mCmdBuilder;
	}

	public long getSendJiffy() {
		return mSendJiffy;
	}

	public String getLastSendCMD() {
		return mLastSendCMD;
	}
	
	public synchronized void setLastSendCMD(String str) {
		mLastSendCMD = str;
	}

	public synchronized boolean SendMutexLocked() {
		if(mSendMutex == enumMutex.MUTEX_LOCK)
			return true;
		else
			return false;
	}

	public synchronized void lockSendMutex() {
		mSendMutex = enumMutex.MUTEX_LOCK;
	}
	
	public synchronized void unLockSendMutex() {
		mSendMutex = enumMutex.MUTEX_UNLOCK;
	}

	//cmdString just contain CMD and argument.
	//combine exist data to send. 
	//mCmdBuilder will be like  CMD ARG SP CMD ARG
	private void uartBuildCmd(String cmdString) {
		if(mCmdBuilder.length() == 0)
			mCmdBuilder.append(cmdString);
		else 
			mCmdBuilder.append(uartUtils.CMD_SP_CHAR).append(cmdString);
	}
	
	public synchronized void uartSendCmd() {	
		if (mSendMutex == enumMutex.MUTEX_UNLOCK) {
			lockSendMutex();
			
			String cmdString;
			if(mCmdBuilder.length() > CMD_MAX_LENGTH - CMD_WRAP_LENGTH) {
				int index;
				StringBuilder loopTemp = mCmdBuilder;
				while((index = loopTemp.lastIndexOf(String.valueOf(uartUtils.CMD_SP_CHAR))) != -1) {
					if(index <= CMD_MAX_LENGTH - CMD_WRAP_LENGTH)
						break;
					else
						loopTemp.delete(index, loopTemp.length());
				}
				if(index == -1) {
					Log.i("sendThread.uartSendCmd", "CMD and ARG is too long");
					mCmdBuilder = mCmdBuilder.delete(0, mCmdBuilder.length());//clear mCmdBuilder
					return;
				}
				
				cmdString = mCmdBuilder.substring(0, index);
				mCmdBuilder = mCmdBuilder.delete(0, index + 1);
			} else {
				cmdString = mCmdBuilder.toString();
				mCmdBuilder = mCmdBuilder.delete(0, mCmdBuilder.length());//clear mCmdBuilder
			}
			
			cmdString += uartUtils.CMD_ETX_CHAR;//append the ETX character.
				
			uartHardwareSend(getAsciiCrc(cmdString));
			mSendJiffy = SystemClock.elapsedRealtime();
			mLastSendCMD = cmdString;
			mUartUtils.getReadThread().clearNakTimes();//clear NAK count when uart send success.
		}
	}
	
	
	public synchronized void uartResendCmd() {	
		if(mLastSendCMD != null) {
			uartHardwareSend(getAsciiCrc(mLastSendCMD));
			mSendJiffy = SystemClock.elapsedRealtime();
		}
	}
	
	private void uartHardwareSend(byte[] sendData) {
		if (sendData == null)
			return;
		
		try{
			if(null != mUartUtils.serialPort.mFd)
				mUartUtils.serialPort.writeBytes(sendData);
		}
		catch (NullPointerException e){
			dumpStack();
		}
	}
	
	private byte[] getAsciiCrc(String cmdStr) {
		try {
			byte[] cmdNoCrc = cmdStr.getBytes("ASCII");
			byte[] cmdByteArray = new byte[cmdNoCrc.length + CMD_CRC16_LENGTH];
			System.arraycopy(cmdNoCrc, 0, cmdByteArray, 0, cmdNoCrc.length);
			
			short crc = CRC16.compute_crc_16(cmdNoCrc, cmdNoCrc.length);
			String str = Integer.toHexString(crc & 0xFFFF).toUpperCase(Locale.US);
			while(str.length() < uartUtils.CMD_CRC16_LENGTH)
				str = '0' + str;
				
			byte[] crcAscii = str.getBytes("ASCII");
			System.arraycopy(crcAscii, 0, cmdByteArray, cmdNoCrc.length, CMD_CRC16_LENGTH);
			return cmdByteArray;
		} catch (UnsupportedEncodingException e1) {
			Log.i("sendThread.uartSendCmd", "UnsupportedEncodingException");
			return null;
		}
	}
	
}
