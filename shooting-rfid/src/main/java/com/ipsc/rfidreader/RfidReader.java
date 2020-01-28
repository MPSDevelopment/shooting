package com.ipsc.rfidreader;

import com.handheld.UHF.UhfManager;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;

public class RfidReader {

	private static final int MEM_BANK = UhfManager.EPC;

	private UhfManager manager = null; // UhfManager.getInstance();

//	private int sensitive = 0;
	private int power = 26; // rate of work
//	private int area = UhfManager.WorkArea_China2;
//	private int frequency = 0;

	private int addr = 0;
	private int length = 1;

	private byte[] accessPassword = { 0, 0, 0, 0 };

	private ArrayList<String> listepc = new ArrayList<String>();
	private ArrayList<EPC> listEPC = new ArrayList<EPC>();

	private boolean runFlag = true;
	private boolean startFlag = false;

//	public String show() {
//		String rfidMark = "";
//
//		List<byte[]> epcList;
//
//		epcList = manager.inventoryRealTime();
//
//		if (epcList != null && !epcList.isEmpty()) {
//			for (byte[] epc : epcList) {
//				rfidMark = Tools.Bytes2HexString(epc, epc.length);
//			}
//		}
//
//		return "Here must be mark -> " + rfidMark;
//	}

	public String start() {
		startFlag = true;
		return "Started";
	}

	public String stop() {
		startFlag = false;
		return "Stopped";
	}

	public String show() {

		String result = initialize();

		if (accessPassword.length != 4) {
//			return result + "\n Wrong password";
			return "Wrong password";
		}

		List<byte[]> epcList = manager.inventoryRealTime();

		// read data
		byte[] data = manager.readFrom6C(MEM_BANK, addr, length, accessPassword);
		if (data != null && data.length > 1) {
			String dataStr = Tools.Bytes2HexString(data, data.length);
			return dataStr.replaceFirst("FFFF", "").replaceFirst("0", "");
			// result += "\n Here must be mark -> " + dataStr;
		} else {
//			return "Wrong mark, List EPC size is " + epcList.size();
			return "Wrong mark";
			// result += "\n Wrong mark for hardware -> " + (manager.getFirmware() == null || manager.getFirmware().length == 0 ? "Hardware not detected" : Tools.Bytes2HexString(manager.getFirmware(), manager.getFirmware().length));
		}

//		List<byte[]> epcList = manager.inventoryRealTime(); // inventory real time
//
//		for (byte[] epc : epcList) {
//			String epcStr = Tools.Bytes2HexString(epc, epc.length);
//			result += "\n Code is " + epcStr;
//		}
//		
//		result += "\n List EPC size is  " + listepc.size();
//
//		return result;
	}

	public String write(String mark) {

		String result = initialize();

		if (accessPassword.length != 4) {
//			return result + "\n Wrong password";
			return "Wrong password";
		}

		if (mark.length() % 4 != 0) {
			return "Wrong mark";
		}

		byte[] data = Tools.HexString2Bytes(mark);

		boolean writeFlag = manager.writeTo6C(accessPassword, MEM_BANK, addr, data.length / 2, data);

		if (writeFlag) {
			return "Mark has been successfully wrote";
		}

		return "Cannot write a mark";
	}

	public String getList() {
		List<byte[]> epcList = manager.inventoryRealTime(); // inventory real time

		String result = "";

		for (byte[] epc : epcList) {
			String epcStr = Tools.Bytes2HexString(epc, epc.length);
			result += "\n Code is " + epcStr;
		}

		result += "\n List EPC size is  " + listepc.size();

		return result;
	}

	public void create() {

//		// start inventory thread
//		Thread thread = new InventoryThread();
//		thread.start();
	}

	public String initialize() {

		String result = "";

		if (manager != null) {
			return "Manager was already initialized";
		}

//		String powerString = "";
//		switch (UhfManager.Power) {
//		case SerialPort.Power_3v3:
//			powerString = "power_3V3";
//			break;
//		case SerialPort.Power_5v:
//			powerString = "power_5V";
//			break;
//		case SerialPort.Power_Scaner:
//			powerString = "scan_power";
//			break;
//		case SerialPort.Power_Psam:
//			powerString = "psam_power";
//			break;
//		case SerialPort.Power_Rfid:
//			powerString = "rfid_power";
//			break;
//		default:
//			break;
//		}

		UhfManager.Port = 13;

		manager = UhfManager.getInstance();

		if (manager == null) {
			return "Serial port failed";
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		manager.setOutputPower(power);
		// very high
		manager.setSensitivity(0);
//		manager.setWorkArea(area);

		byte[] version_bs = manager.getFirmware();

		result = "Manager has been initiated.";
		result += "\n Port is " + UhfManager.Port + "Power is " + UhfManager.Power;
		result += "\n Frequency is " + manager.getFrequency();
		result += "\n Rate is " + UhfManager.BaudRate;

//		manager.stopInventoryMulti();

		if (version_bs != null) {
			result += "\n Firmware " + new String(version_bs);
		}

		// start inventory thread
//		Thread thread = new InventoryThread();
//		thread.start();

		return result;
	}

	protected void pause() {
		manager.close();
	}

	protected void destroy() {
		if (manager != null) {
			manager.close();
		}
	}

	/**
	 * Inventory EPC Thread
	 */
	class InventoryThread extends Thread {
		private List<byte[]> epcList;

		@Override
		public void run() {
			super.run();
			while (runFlag) {
				if (startFlag) {
					// manager.stopInventoryMulti()
					epcList = manager.inventoryRealTime(); // inventory real time
					if (epcList != null && !epcList.isEmpty()) {
						// play sound
						for (byte[] epc : epcList) {
							String epcStr = Tools.Bytes2HexString(epc, epc.length);
							addToList(listEPC, epcStr);
						}
					}
					epcList = null;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	// EPC add to LISTVIEW
	private void addToList(final List<EPC> list, final String epc) {

		if (list.isEmpty()) {
			EPC epcTag = new EPC();
			epcTag.setEpc(epc);
			epcTag.setCount(1);
			list.add(epcTag);
			listepc.add(epc);
		} else {
			for (int i = 0; i < list.size(); i++) {
				EPC mEPC = list.get(i);
				// list contain this epc
				if (epc.equals(mEPC.getEpc())) {
					mEPC.setCount(mEPC.getCount() + 1);
					list.set(i, mEPC);
					break;
				} else if (i == (list.size() - 1)) {
					// list doesn't contain this epc
					EPC newEPC = new EPC();
					newEPC.setEpc(epc);
					newEPC.setCount(1);
					list.add(newEPC);
					listepc.add(epc);
				}
			}
		}
	}

}
