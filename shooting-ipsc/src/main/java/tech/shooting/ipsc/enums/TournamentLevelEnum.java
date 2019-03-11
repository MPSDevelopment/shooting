package tech.shooting.ipsc.enums;

/**
 * LEVEL I Match (example: local weekend club match)
 * LEVEL II Match (example: inter-club monthly match)
 * LEVEL III Match (example: large Regional match)
 * LEVEL IV Match (example: Continental Championship)
 * LEVEL V Match (example: World Shoot)
 */
public enum TournamentLevelEnum {

	LEVEL_1("Level 1"), LEVEL_2("Level 2"), LEVEL_3("Level 3"), LEVEL_4("Level 4"), LEVEL_5("Level 5");

	private String value;

	TournamentLevelEnum (String value) {
		this.value = value;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}}
