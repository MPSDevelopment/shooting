package tech.shooting.ipsc.enums;

public enum TournamentTypeEnum {

	HANDGUN_SHOTGUN_RIFLE("Handgun, shotgun and rifle"), HANDGUN_RIFLE("Handgun and rifle"), HANDGUN_SHOTGUN("Handgun and shotgun"), SHOTGUN_RIFLE("Shotgun and rifle"), RIFLE("Rifle"), SHOTGUN("Shotgun"), HANDGUN("Handgun");

	private String value;

	TournamentTypeEnum(String value) {
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
