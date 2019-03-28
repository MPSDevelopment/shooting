package tech.shooting.ipsc.enums;

import java.util.ArrayList;
import java.util.List;

public enum TypeMarkEnum {
	RFID("RFID"),
	NUMBER("NUMBER");

	private String value;

	TypeMarkEnum (String value) {
		this.value = value;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}

	public static List<String> getList () {
		List<String> res = new ArrayList<>();
		for(TypeMarkEnum issue : TypeMarkEnum.values()) {
			res.add(issue.getValue());
		}
		return res;
	}
}