package com.hedymed.net;

public class netMessage {
	private short startFlag;
	private byte msgType;
	private byte cmd;
	private int dataLen;
	private int fileNameLen;
	private int fileLen;
	private byte[] cmdData;
	private byte[] fileNameData;
	private byte[] fileData;
	private byte tail;
}

