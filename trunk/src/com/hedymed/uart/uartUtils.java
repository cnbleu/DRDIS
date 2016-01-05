package com.hedymed.uart;

import static com.hedymed.drdissys.appConfig.NAK_THRESHOLD;
import static com.hedymed.drdissys.appConfig.UART_WAITE_TIME;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.hedymed.drdissys.MainActivity;

enum enumUartSendFlag {
	NEXT_FRAME, RESEND_FLAG;
}

public class uartUtils {
	public static final String UART_SEND_DATA_KEY = "uart_send";
	public static final int CMD_MAX_LENGTH = 100;
	public static final char CMD_ETX_CHAR = ((char)0x03);
	public static final char CMD_SP_CHAR = ((char)0X20);
	public static final char CMD_NAK_CHAR = ((char)0x15);
	public static final int CMD_CRC16_LENGTH = 4;
	public static final int CMD_WRAP_LENGTH = 5;
	
	public static final int NEW_CMD_FLAG = 0;
	public static final int NEXT_FRAME_FLAG = 1;
	public static final int RESEND_FLAG = 2;
	public static final int ACK_FLAG = 3;
	
	public SerailPortOpt serialPort;
	public OutputStream mOutputStream;
	public InputStream mInputStream;
	private readThread mReadThread;
	private static sendThread mSendThread;
	private MainActivity mMainActivity;
	private Timer mUartCheckTimer;
	
	public uartUtils(Context context) {
		if(context instanceof MainActivity)
			mMainActivity = (MainActivity)context;
		
		serialPort = new SerailPortOpt();
	}

	public void uartOpen(int devNum, int speed, int dataBits, int stopBit, int parity) {
		serialPort.mDevNum = devNum;
		serialPort.mSpeed = speed;
		serialPort.mDataBits = dataBits;
		serialPort.mStopBits = stopBit;
		serialPort.mParity = parity;

		openSerialPort();
		
		mUartCheckTimer = new Timer();
		mUartCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				uartTimerCheck();
			}
		}, 0, 1000);

		// mReadThread.interrupt();
	}

	private void openSerialPort() {
		if (serialPort.mFd == null) {
			serialPort.openDev(serialPort.mDevNum);
			Log.i("uart port operate", "open uart " + serialPort.mDevNum);

			serialPort.setSpeed(serialPort.mFd, serialPort.mSpeed);
			Log.i("uart port operate", "set uart " + serialPort.mDevNum + " at speed " + serialPort.mSpeed);

			serialPort.setParity(serialPort.mFd, serialPort.mDataBits, serialPort.mStopBits, serialPort.mParity);
			Log.i("uart port operate", "set uart " + serialPort.mDevNum + " data bit " + serialPort.mDataBits
					+ " stop bit " + serialPort.mStopBits + " parity form " + serialPort.mParity);

			mInputStream = serialPort.getInputStream();
			mOutputStream = serialPort.getOutputStream();
			mSendThread = new sendThread(this, "uartSendThread");
			mSendThread.start();
			mReadThread = new readThread(this, "uartReadThread");
			mReadThread.start();
		}
	}

	public void uartClose() {
		closeSerialPort();
		serialPort = null;
	}

	private void closeSerialPort() {

		if (mReadThread != null) 
			mReadThread.interrupt();
		
		if(mSendThread != null)
			mSendThread.stopItself();
		
		if(mUartCheckTimer != null)
			mUartCheckTimer.cancel();

		if (serialPort.mFd != null) {
			serialPort.closeDev(serialPort.mFd);
			Log.i("uart port operate", "close uart " + serialPort.mDevNum);
		}
	}
	
	public readThread getReadThread() {
		return mReadThread;
	}

	//cmdString 包含命令和参数
	public static void sendToSendThread(String cmdString, int... what) {
		Message msg;
		if(what.length == 0) 
			msg = Message.obtain(mSendThread.mSendHandler, NEW_CMD_FLAG);
		else 
			msg = Message.obtain(mSendThread.mSendHandler, what[0]);
		
		Bundle bundle = new Bundle();
		bundle.putCharSequence(UART_SEND_DATA_KEY, cmdString);
		msg.setData(bundle);
		mSendThread.mSendHandler.sendMessage(msg);
	}
	
	//return true will send next frame.
	private boolean judgePrefix(String[] cmdAndArg) {
		String str = mSendThread.getLastSendCMD();
		if(str != null) {
			String[] lastCmdArg = str.split("(?<=[A-Z])(?![A-Z])");
			if(lastCmdArg[0].equals(cmdAndArg[0])) {
				int index;
				if((index = str.indexOf(CMD_SP_CHAR)) != -1) {
					mSendThread.setLastSendCMD(str.substring(index + 1));
					return false;//等待所有命令被应答
				} else {
					mSendThread.setLastSendCMD(null);
					return true;
				}
			} 
		}
		
//		//接收到非应答命令
//		if(cmdAndArg[1].matches("(?<=^)[a-z0-9]+-?[a-z0-9]*")) {//参数不是[a-z0-9]时不在此发送应答
//			MainActivity.sendToUiThread(cmdAndArg);
//			sendToSendThread(cmdAndArg[0] + cmdAndArg[1], CMD_ACK_FLAG);
//		}
		return false;
	}
	
	public void uartUtilsSetErrString(final String errStr) {
		if(Thread.currentThread() == Looper.getMainLooper().getThread())
			mMainActivity.setErrDisText(errStr);
		else {
			mMainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mMainActivity.setErrDisText(errStr);
				}
			});
		}
	}
	
	private void uartTimerCheck() {
		if(mSendThread.SendMutexLocked()) {
			if(SystemClock.elapsedRealtime() - mSendThread.getSendJiffy() > UART_WAITE_TIME) {
				uartUtilsSetErrString("in timer thread, the UART connection lost...");
				mSendThread.unLockSendMutex();
				mSendThread.setLastSendCMD(null);
			}
		}
	}
	
	public void checkSendFun(String... cmdAndArg) {
		if(cmdAndArg.length < 2) {
			if( mReadThread.getNakTimes() >= NAK_THRESHOLD) {
				uartUtilsSetErrString("in UARTrecv thread, the UART connection lost...");
            	mSendThread.unLockSendMutex();
            	mSendThread.setLastSendCMD(null);//for next frame send
			} else
				sendToSendThread(null, RESEND_FLAG);//if receive a NAK, resend the last cmd at sendThread;
		} else {
			if(mSendThread.SendMutexLocked()) {
				if(judgePrefix(cmdAndArg)) {
		            mSendThread.unLockSendMutex();
		            if(mSendThread.getCmdBuilder().length() != 0)
		            	sendToSendThread(null, NEXT_FRAME_FLAG);// send next CMD.
	            } 
			} else {//接收到非应答命令
				if(cmdAndArg[1].matches("(?<=^)([a-z0-9]+(-[a-z0-9]+)?|[+-][0-9]{1,3})$")) {//参数检查正确
					mMainActivity.sendToUiThread(cmdAndArg);
					sendToSendThread(cmdAndArg[0] + cmdAndArg[1], ACK_FLAG);
				} else
					uartUtils.sendToSendThread("ER???");
			}
		}
	}

}
