package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.SubjectsName;

import java.util.ArrayList;
import java.util.List;

public enum Subject {
	FIRE("FIRE", "Огневая подготовка"),
	PHYSICAL("PHYSICAL", "Физическая подготовка"),
	MILITARY_MEDICIAL("MILITARY_MEDICIAL", "Военно-медицинская подготовка"),
	SPECIAL_TACTICAL("SPECIAL_TACTICAL", "Тактико-специальная подготовка"),
	TACTICAL_AND_SERVICE_COMBAT_USE("TACTICAL_AND_SERVICE_COMBAT_USE", "Тактико-служебно боевого применения"),
	ALTITUDE("ALTITUDE", "Высотная подготовка"),
	GENERAL_MILITARY_REGULATIONS("GENERAL_MILITARY_REGULATIONS", "Общевоинские уставы"),
	MILITARY_ENGINEERING("MILITARY_ENGINEERING", "Военно-инженерная подготовка"),
	RADIATION_CHEMICAL_AND_BIOLOGICAL_PROTECTION("RADIATION_CHEMICAL_AND_BIOLOGICAL_PROTECTION", "Радиационная, химическая и биологическая защита"),
	COMMUNICATION("COMMUNICATION", "Подготовка по связи"),
	MILITARY_TOPOGRAPHY("MILITARY_TOPOGRAPHY", "Военная топография"),
	SPECIAL_TRAINING_IN_PROFESSIONAL_CATEGORIES("SPECIAL_TRAINING_IN_PROFESSIONAL_CATEGORIES", "Специальная подготовка по категориям специалистов");

	private String useName;

	private String name;

	Subject (String useName, String name) {
		this.useName = useName;
		this.name = name;
	}

	public String getName () {
		return name;
	}

	public static List<SubjectsName> getList () {
		List<SubjectsName> res = new ArrayList<>();
		for(Subject s : Subject.values()) {
			res.add(new SubjectsName().setName(s.getName()).setUseName(s.getUseName()));
		}
		return res;
	}

	@Override
	public String toString () {
		return String.valueOf(name);
	}

	public String getUseName () {
		return useName;
	}
}
