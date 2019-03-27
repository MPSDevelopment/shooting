package tech.shooting.ipsc.enums;

public enum TypeOfPresence {
	UNDEFINED(1, "Unidentified location"),
	MISSION(2, "In official journey"),
	SICK_LEAVE(3, "Home because is sick"),
	INFIRMARY(4, "In infirmary"),
	HOSP(5, "In hospital"),
	OUTFIT(6, "In order"),
	DAY_OFF(7, "Day off"),
	PRESENT(8, "Is present");

	private String state;

	private Integer id;

	TypeOfPresence (Integer id, String state) {
		this.state = state;
		this.id = id;
	}

	public static Integer getCount () {
		return TypeOfPresence.values().length;
	}

	@Override
	public String toString () {
		return String.valueOf(state);
	}

	public String getState () {
		return state;
	}

	public int getId () {
		return id;
	}
}
