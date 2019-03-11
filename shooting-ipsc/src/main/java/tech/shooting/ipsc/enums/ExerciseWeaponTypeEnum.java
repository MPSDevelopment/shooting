package tech.shooting.ipsc.enums;

public enum ExerciseWeaponTypeEnum {

	RIFLE("Rifle"), SHOTGUN("Shotgun"), HANDGUN("Handgun");

	private String value;

	ExerciseWeaponTypeEnum (String value) {
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
