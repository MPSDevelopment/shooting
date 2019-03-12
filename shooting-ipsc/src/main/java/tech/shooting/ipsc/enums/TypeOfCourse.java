package tech.shooting.ipsc.enums;

public enum TypeOfCourse {
	ShortCourse("Short Course");
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
