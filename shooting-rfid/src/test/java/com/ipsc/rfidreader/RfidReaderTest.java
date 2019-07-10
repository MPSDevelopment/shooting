package com.ipsc.rfidreader;

import org.junit.jupiter.api.Test;

import cn.pda.serialport.Tools;

public class RfidReaderTest {
	
	private byte[] accessPassword = { 0, 0, 0, 0};

	@Test
	public void checkTools() {
		byte[] bytes = Tools.HexString2Bytes("12345678");

		System.out.println(Tools.Bytes2HexString(bytes, bytes.length));
		System.out.println(new String(bytes));

		System.out.println(Tools.Bytes2HexString(accessPassword, accessPassword.length));

	}
}
