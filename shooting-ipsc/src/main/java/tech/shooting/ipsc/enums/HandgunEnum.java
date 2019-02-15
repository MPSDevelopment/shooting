package tech.shooting.ipsc.enums;

public enum HandgunEnum {

	OPEN("Open"), STANDARD("Standard"), MODIFIED("Modified"), PRODUCTION("Production"), RESOLVER("Resolver"), CLASSIC("Classic");

	private String value;

	HandgunEnum(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public String getValue() {
		return value;
	}
}
