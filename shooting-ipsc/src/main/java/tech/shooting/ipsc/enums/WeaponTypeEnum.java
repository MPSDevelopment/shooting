package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.pojo.TypeWeapon;

import java.util.ArrayList;
import java.util.List;

public enum WeaponTypeEnum {
	RIFLE(1, "Rifle"),
	SHOTGUN(2, "Shotgun"),
	HANDGUN(3, "Handgun");

	private String value;

	private int id;

	WeaponTypeEnum (int id, String value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}

	public int getId () {
		return id;
	}

	public static List<TypeWeapon> getList () {
		List<TypeWeapon> result = new ArrayList<>();
		for(WeaponTypeEnum value : WeaponTypeEnum.values()) {
			result.add(new TypeWeapon().setId(value.id).setName(value.value));
		}
		return result;
	}
	public static Integer getCount(){
		return WeaponTypeEnum.values().length;
	}
}
