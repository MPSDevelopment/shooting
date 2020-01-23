package com.ipsc.rfidreader;

import org.junit.jupiter.api.Test;

import cn.pda.serialport.Tools;

public class RfidReaderTest {
	
	private static final String code = "12345678";
	
	private static final String code3 = "08Е3";
	
	private byte[] accessPassword = { 0, 0, 0, 0};

	@Test
	public void checkTools() {
		byte[] bytes = Tools.HexString2Bytes(code);

		System.out.println(Tools.Bytes2HexString(bytes, bytes.length));
		System.out.println(new String(bytes));

	}
	
	@Test
	public void checkTools3Symbols() {
		byte[] bytes = { 8, 13, 3};
		
		System.out.println(Tools.Bytes2HexString(bytes, bytes.length));
	}
	
	@Test
	public void checkReplace() {
		String code = code3.replaceFirst("0", "");
		System.out.println(code);
		code = "080E3".replaceFirst("0", "");
		System.out.println(code);
	}
}
