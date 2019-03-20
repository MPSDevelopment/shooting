package tech.shooting.ipsc.enums;

public enum TypeMarkEnum {
	RFID("rfid"),
	NUMBER("number");

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
}