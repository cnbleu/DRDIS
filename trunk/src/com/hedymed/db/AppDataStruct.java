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
	//"CURR_FRAGMENT"

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
	//"ASENABLE"
	//"TRA"
	//"FIE"
	//"DC" Dose ѡ��
	//"EC"  �عⲹ��
	//"AEC"
	//"AECENABLE"
	//"SOD"

	static {
		String[] cmdArray = appConfig.UART_SUPPORT_CMD.split(";");
		appData.put("CURR_FRAGMENT", 0);
		appData.put("ASENABLE", 1);
		appData.put("AECENABLE", 1);
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
