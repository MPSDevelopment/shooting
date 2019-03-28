package tech.shooting.ipsc.enums;

public enum Subject {
	FIRE("Огневая подготовка"),
	PHYSICAL("Физическая подготовка"),
	MILITARY_MEDICIAL("Военно-медицинская подготовка"),
	SPECIAL_TACTICAL("Тактико-специальная подготовка"),
	TACTICAL_AND_SERVICE_COMBAT_USE("Тактико-служебно боевого применения"),
	ALTITUDE("Высотная подготовка"),
	GENERAL_MILITARY_REGULATIONS("Общевоинские уставы"),
	MILITARY_ENGINEERING("Военно-инженерная подготовка"),
	RADIATION_CHEMICAL_AND_BIOLOGICAL_PROTECTION("Радиационная, химическая и биологическая защита"),
	COMMUNICATION("Подготовка по связи"),
	MILITARY_TOPOGRAPHY("Военная топография"),
	SPECIAL_TRAINING_IN_PROFECCIONAL_CATEGORIES("Специальная подготовка по категориям специалистов");

	private String name;

	Subject (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	@Override
	public String toString () {
		return String.valueOf(name);
	}
}
