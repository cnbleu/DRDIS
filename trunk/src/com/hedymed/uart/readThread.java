package com.hedymed.uart;

import static com.hedymed.uart.uartUtils.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hedymed.drdissys.appConfig;

import android.util.Log;

public class readThread extends Thread {
	private uartUtils mUartUtils;
	private byte[] buf = new byte[512];
	private StringBuilder mRecvCmdStr;
	private int mNakTimes;
	private List<String> mAllCmdSupport;

	public readThread(uartUtils uart, String threadName) {
		super(threadName);
		this.mUartUtils = uart;
		mRecvCmdStr = new StringBuilder();
		mAllCmdSupport = new ArrayList<String>();
		addSupportCmd();
	}
	
	@Override
	public void run() {
		int length = 0;
		
		try {
			while (!isInterrupted()) {
				if (mUartUtils.mInputStream == null)
					return;
				
				length = mUartUtils.serialPort.readBytes(buf);
				if(length > 0 ) 
					dispatchCmd(buf, length);
				
				sleep(50);
			}
		}
		catch (NullPointerException e){
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void addSupportCmd() {
		String[] cmdArray = appConfig.UART_SUPPORT_CMD.split(";");
		mAllCmdSupport = Arrays.asList(cmdArray);
	}
	
	public int getNakTimes() {
		return mNakTimes;
	}

	public void clearNakTimes() {
		this.mNakTimes = 0;
	}

	//返回剩余数据长度
	private void uartDecoder(List<String> cmdList, final byte[] srcBuf, int size) {
		int index = 0;
		
		try {
			//add last receive data. ex:last time received CMD ARG and this time received ETX CRC
			mRecvCmdStr.append(new String(srcBuf, 0, size, "ASCII"));
			
			while((index = mRecvCmdStr.indexOf(String.valueOf(uartUtils.CMD_NAK_CHAR))) != -1) {
				cmdList.add(String.valueOf(CMD_NAK_CHAR));
				mRecvCmdStr.delete(index, index + 1);// remove NAK character from cmd buffer.
			}
		
			while((index = mRecvCmdStr.indexOf(String.valueOf(uartUtils.CMD_ETX_CHAR))) != -1
					&& mRecvCmdStr.length() > index + CMD_CRC16_LENGTH)	{//if find ETX flag
				int cmdLen = index + CMD_WRAP_LENGTH;
				String cmdStr = mRecvCmdStr.substring(0, cmdLen);
				mRecvCmdStr.delete(0, cmdLen);
				
				byte[] array = cmdStr.getBytes("ASCII");
				int crcPos = index + 1;
				int crc = Integer.parseInt(cmdStr.substring(crcPos), 16);
				array[crcPos] = (byte) ((crc >> 8) & 0xFF);
				array[crcPos + 1] = (byte) (crc & 0xFF);// big endian
				
				//check CRC
				if(CRC16.check_crc16(array, array.length - 2)) {
					int startPos = 0;
					while((index = cmdStr.indexOf(String.valueOf(uartUtils.CMD_SP_CHAR), startPos)) != -1) {
						cmdList.add(cmdStr.substring(startPos, index));
						startPos = index + 1;
					}
					cmdList.add(cmdStr.substring(startPos, cmdStr.length() - CMD_WRAP_LENGTH));//the last CMD ARG
				} else
					uartUtils.sendToSendThread(String.valueOf(CMD_NAK_CHAR));// send NAK if CRC check failure
			}
		}
		catch (UnsupportedEncodingException e) {
			Log.i("uartReadThread", "ASCII decode error");
		}
		catch (NumberFormatException e) {
			Log.i("uartReadThread", "cmd CRC receive error");
		}
        
	}
	
	
	private void dispatchCmd(byte[] srcData, int srcSize) {
		List<String> cmdList = new ArrayList<String>();
		uartDecoder(cmdList, srcData, srcSize);
		
		for (String str : cmdList) {
			if(str.equals(String.valueOf(CMD_NAK_CHAR))) {
				mNakTimes++;
				mUartUtils.checkSendFun(str);
			} else {
				mNakTimes = 0;//clear NAK counter.
				String[] cmdAndArg = new String[2];
				if(detectSupport(cmdAndArg, str)) 
					mUartUtils.checkSendFun(cmdAndArg);
				else
					uartUtils.sendToSendThread("ER???");
			}
		}
	}
	
	//return true if cmd is supported. or else false is returned.
	private boolean detectSupport(String[] cmdAndArg, String cmdWithArg) {
		String[] cmdParsed = cmdWithArg.split("(?<=[A-Z])(?![A-Z])");
		
		if(cmdParsed.length < 2)//if cmd format is error.
			return false;
		
		cmdAndArg[0] = cmdParsed[0];//cmd
		cmdAndArg[1] = cmdParsed[1];//arg
		
		Iterator<String> iter = mAllCmdSupport.iterator();
		while(iter.hasNext()) {
			if(iter.next().equals(cmdAndArg[0])) 
				return true;
		}
		return false;
	}
	
}
