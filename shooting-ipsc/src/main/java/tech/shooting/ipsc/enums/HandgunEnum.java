package tech.shooting.ipsc.enums;

/**
 * Класс пистолет
 */
public enum HandgunEnum {

	OPEN("Open"), STANDARD("Standard"), CLASSIC("Classic"), PRODUCTION("Production"), REVOLVER("Revolver"), MODIFIED("Modified");

	private String value;

	HandgunEnum (String value) {
		this.value = value;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}}
