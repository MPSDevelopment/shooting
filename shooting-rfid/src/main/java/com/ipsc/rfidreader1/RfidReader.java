package com.ipsc.rfidreader1;

import com.handheld.UHF.UhfManager;

import java.util.List;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;

public class RfidReader {

	private UhfManager manager = UhfManager.getInstance();

	private int sensitive = 0;
	private int power = 0;// rate of work
	private int area = 0;
	private int frequency = 0;

	public String show() {
		String rfidMark = "";

		List<byte[]> epcList;

		epcList = manager.inventoryRealTime();

		if (epcList != null && !epcList.isEmpty()) {
			for (byte[] epc : epcList) {
				rfidMark = Tools.Bytes2HexString(epc, epc.length);
			}
		}

		return "Here must be mark -> " + rfidMark;
	}

	public String initialize() {

		String powerString = "";
		switch (UhfManager.Power) {
		case SerialPort.Power_3v3:
			powerString = "power_3V3";
			break;
		case SerialPort.Power_5v:
			powerString = "power_5V";
			break;
		case SerialPort.Power_Scaner:
			powerString = "scan_power";
			break;
		case SerialPort.Power_Psam:
			powerString = "psam_power";
			break;
		case SerialPort.Power_Rfid:
			powerString = "rfid_power";
			break;
		default:
			break;
		}
		manager = UhfManager.getInstance();
		if (manager == null) {
			return "Serial port failed";
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		Log.e("", "value" + power);
		manager.setOutputPower(power);
		manager.setWorkArea(area);
		byte[] version_bs = manager.getFirmware();
		if (version_bs != null) {
			return "Manager has been initiated " + new String(version_bs) + ". Power string is " + powerString;
		}

		return "Manager has not been initiated" + ". Power string is " + powerString;
	}

	protected void pause() {
		manager.close();
	}

	protected void destroy() {
		if (manager != null) {
			manager.close();
		}
	}
}
