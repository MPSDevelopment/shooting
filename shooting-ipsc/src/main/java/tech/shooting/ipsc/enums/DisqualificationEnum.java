package tech.shooting.ipsc.enums;

public enum DisqualificationEnum {
	DISQUALIFICATION, ABSENT, INJURED, BROKEN_RULE, OTHER;


	public static int getCount() {
		return DisqualificationEnum.values().length;
	}

}
