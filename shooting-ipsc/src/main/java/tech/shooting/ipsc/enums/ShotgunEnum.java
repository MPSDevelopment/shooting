package tech.shooting.ipsc.enums;

/**
 * Класс ружье
 */
public enum ShotgunEnum {

	OPEN("Open"), MODIFIED("Modified"), STANDARD("Standard"), PUMP("Pump");

	private String value;

	ShotgunEnum (String value) {
		this.value = value;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}}
