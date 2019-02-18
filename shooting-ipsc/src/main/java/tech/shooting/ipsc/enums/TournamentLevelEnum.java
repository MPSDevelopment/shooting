package tech.shooting.ipsc.enums;

public enum TournamentLevelEnum {

	LEVEL_1("Level 1"), LEVEL_2("Level 2"), LEVEL_3("Level 3"), LEVEL_4("Level 4"), LEVEL_5("Level 5");

	private String value;

	TournamentLevelEnum(String value) {
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
