package tech.shooting.ipsc.enums;

public enum DisqualificationEnum {
	DISQUALIFICATION("DISQUALIFICATION", "DISQUALIFICATION"), ABSENT("ABSENT", "ABSENT"), INJURED("INJURED", "INJURED"), BROKEN_RULE("BROKEN_RULE", "BROKEN_RULE");

	private String type;

	private String name;

	DisqualificationEnum(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public static int getCount() {
		return DisqualificationEnum.values().length;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.valueOf(type);
	}

}
