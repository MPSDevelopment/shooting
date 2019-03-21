package tech.shooting.ipsc.enums;

public enum TypeOfCourse {
	ShortCourse("Short Course"),
	MediumCourse("Medium Course");

	private String typeOfCourse;

	TypeOfCourse (String typeOfCourse) {
		this.typeOfCourse = typeOfCourse;
	}

	public String getTypeOfCourse () {
		return typeOfCourse;
	}

	@Override
	public String toString () {
		return "TypeOfCourse{" + "typeOfCourse='" + typeOfCourse + '\'' + '}';
	}
}
