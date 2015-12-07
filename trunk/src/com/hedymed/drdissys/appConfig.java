package com.hedymed.drdissys;

final public class appConfig {
	public static final int UART_TXFIFO_SIZE = 64;
	public static final int UART_RXFIFO_SIZE = 64;
	public static final int UART_BLOCK_MAXIMUM = 64;
	public static final int MOST_PARA_LENGTH = 64;
	public static final int UART_BLOCK_LENGTH = 65;
	public static final int UART_WAITE_TIME = 3000;//30s
	public static final int NAK_THRESHOLD = 3;//
	public static final byte CRC16_LENGTH = 2;
	public static final byte HANDSHAKE_CMD_LENGTH = 8; 
	public static final byte HANDSHAKE_CRC_INDEX = 6; 
	public static final int UART_CIRCLE_BUFFER_SIZE = 128;
	public static final String UART_SUPPORT_CMD = "AEC;CM;APR;AG;BOD;POS;DC;EC;ET;FOC;SID;SOD;AS;"
								+ "FIE;TRA;KV;MA;MS;MAS;NAM;SEX;ID;AGE;RHA;RVA;AI;EMG;ES;TC;ER;"
								+ "GRID;GRID_SID;GRID_RATE;GRID_MATERIAL;FPD;ANG";
}
