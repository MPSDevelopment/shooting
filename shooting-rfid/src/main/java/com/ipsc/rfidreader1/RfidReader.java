package com.ipsc.rfidreader1;

import com.handheld.UHF.UhfManager;

import java.util.List;

import cn.pda.serialport.Tools;

public class RfidReader {

	private UhfManager manager = UhfManager.getInstance();

	public String show() {
		String rfidMark = "";

		List<byte[]> epcList;
		
		epcList = manager.inventoryRealTime();

		if (epcList != null && !epcList.isEmpty()) {
			for (byte[] epc : epcList) {
				rfidMark = Tools.Bytes2HexString(epc, epc.length);
			}
		}

		return "Here must be mark" + rfidMark ;
	}
}
