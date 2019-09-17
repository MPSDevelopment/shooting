package tech.shooting.ipsc.enums;

public enum CompetitionCategoryEnum {
	LADY("Lady"),
	JUNIOR("Junior"),
	SUPER_JUNIOR("Super junior"),
	SENIOR("Senior"),
	SUPER_SENIOR("Super senior");

	private String value;

	CompetitionCategoryEnum (String value) {
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
