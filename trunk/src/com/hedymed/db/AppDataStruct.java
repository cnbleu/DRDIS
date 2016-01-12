package com.hedymed.db;

import java.util.HashMap;
import java.util.Map;

import com.hedymed.drdissys.appConfig;

public class AppDataStruct {
	public static enum expose_status{
		DISABLE_EXPOSE, ENABLE_EXPOSE, PREP_PHASE, EXPOSE_PHASE;//��ֹ�ع�, ʹ���ع�, һ��״̬�� ����״̬
	}
	
	public static enum alignment_status{
		NOT_ALIGNMENT, HAVE_ALIGNMENT;//δ���У��Ѷ���
	}
	
	public static final byte INSERT_RIGHT = 0x01;// FPD ���뵽��λ
	public static final byte INSERT_ERROR = 0x00;// FPD δ���뵽��λ
	public static final byte FPD_WS_H_INSERT = 0x00;// FPD ��Ƭ�ܺ��
	public static final byte FPD_WS_V_INSERT = 0x01;// FPD ��Ƭ������
	public static final byte FPD_FT_H_INSERT = 0x02;// FPD �̶������
	public static final byte FPD_FT_V_INSERT = 0x03;// FPD �̶�������
	public static final byte GRID_MATERIAL_AL = 0x01;// ����դ���� ��
	public static final byte GRID_MATERIAL_CARBON = 0x02;// ����դ���� ̼
	public static final byte FOCUS_SMALL = 0x01;// С����
	public static final byte FOCUS_BIG = 0x00;// �󽹵�
	public static final byte FIELD_TRACE_OFF = 0x00;// ��Ұ׷�ٹ�
	public static final byte FIELD_TRACE_ON = 0x01;// ��Ұ׷�ٿ�
	public static final byte ALIGN_TOP = 0x00;// �϶���
	public static final byte ALIGN_MID = 0x01;// �ж���
	public static final byte ALIGN_BOT = 0x02;// �¶���
	public static final byte AEC_LEFT_FIELD_MASK = 0x01;
	public static final byte AEC_RIGHT_FIELD_MASK = 0x02;
	public static final byte AEC_TOP_FIELD_MASK = 0x04;

	// message what attribute
	public static final byte NEW_fpdActivity = 0x01;
	public static final byte NEW_sampleMode = 0x11;
	public static final byte NEW_EMGStatus = 0x38;
	public static final byte NEW_bodyPicture = 0x12;
	public static final byte NEW_nameText = 0x30;
	public static final byte NEW_sexText = 0x31;
	public static final byte NEW_ageText = 0x33;
	public static final byte NEW_idText = 0x32;
	// public static final byte NEW_pageSwitchPic = 0x15;
	public static final byte NEW_errDisText = 0x40;
	public static final byte NEW_expouseStatusPic = 0x3A;
	//public static final byte NEW_expouseEnable = 0x39;
	public static final byte NEW_alignmentStatusPic = 0x37;
	public static final byte NEW_fpdDirection = 0x3E;

	public static final byte NEW_rha = 0x35;
	public static final byte NEW_rva = 0x36;
	public static final byte NEW_sid = 0x1F;
	public static final byte NEW_grid = 0x3D;
	public static final byte NEW_thermal = 0x3B;
	public static final byte NEW_handshake = (byte) 0xF5;
	// main fragment message what attribute
	public static final byte NEW_HVGPointSelecter = 0x18;
	public static final byte NEW_positionSelecter = 0x15;
	public static final byte NEW_ageSelecter = 0x13;
	public static final byte NEW_bodyTypeSelecter = 0x14;
	public static final byte NEW_focusSelect = 0x1D;
	public static final byte NEW_hvgVoltage = 0x19;
	public static final byte NEW_hvgCurrent = 0x1A;
	public static final byte NEW_hvgMas = 0x1C;
	public static final byte NEW_hvgMs = 0x1B;
	public static final byte NEW_hvgOperator = 0x25;
	// second fragment message what attribute
	public static final byte NEW_radioGroupAlign = 0x21;
	public static final byte NEW_fieldTraceButton = 0x23;
	public static final byte NEW_lightFieldSelecter = 0x22;
	public static final byte NEW_doseSelecter = 0x16;
	public static final byte NEW_compenSelecter = 0x17;
	public static final byte NEW_aecField = 0x10;
	public static final byte NEW_sodEditor = 0x20;
	
	public static final double MAS_FACTOR = 1000;
	
	public static Map<String, String> appStringData = new HashMap<String, String>();
	//"NAM"
	//"SEX"
	//"ID"
	//"APR"
	static {
		appStringData.put("APR", "h09-001");
	}
	
	public static Map<String, Integer> appData = new HashMap<String, Integer>();
	//"EMG"
	//"AGE"
	//"ES"
	//"AI"
	//"FPD"

	//"RHA"
	//"RVA"
	//"SID"
	//"TC"
	//"GRID_SID"
	//"GRID_RATE"
	//"GRID_MATERIAL"
	//"ANG"

/* main fragment*/
	//"ET"
	//"POS"
	//"AG"
	//"BOD"
	//"FOC"
	//"KV"
	//"MA" factor is 10 
	//"MS"  factor is 10 
	//"MAS"  factor is 10.0 

/* second fragment */
	//"AS"
	//"TRA"
	//"FIE"
	//"DC" Dose ѡ��
	//"EC"  �عⲹ��
	//"AEC"
	//"SOD"

	static {
		String[] cmdArray = appConfig.UART_SUPPORT_CMD.split(";");
		for(String str : cmdArray)
			appData.put(str, 0);
	}
	
	// big-endian
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[3] = (byte) (res & 0xff);// ���λ
		targets[2] = (byte) ((res >> 8) & 0xff);// �ε�λ
		targets[1] = (byte) ((res >> 16) & 0xff);// �θ�λ
		targets[0] = (byte) (res >>> 24);// ���λ,�޷������ơ�
		return targets;
	}

	// big-endian
	public static int byte2int(byte[] res, int index, int count) {
		int targets = 0;

		if (count == 4)
			targets = (res[index + 3] & 0xff) | ((res[index + 2] << 8) & 0xff00) | ((res[index + 1] << 24) >>> 8)
					| (res[index] << 24);

		return targets;
	}

}
