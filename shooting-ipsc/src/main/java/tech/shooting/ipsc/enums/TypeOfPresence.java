package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.TypePresent;

import java.util.ArrayList;
import java.util.List;

public enum TypeOfPresence {
	ALL(0, "All"), 
	UNDEFINED(1, "Unidentified location"),
	MISSION(2, "In official journey"),
	SICK_LEAVE(3, "Home because is sick"),
	INFIRMARY(4, "In infirmary"),
	HOSP(5, "In hospital"),
	OUTFIT(6, "In order"),
	DAY_OFF(7, "Day off"),
	PRESENT(8, "Is present"),
	DELAY(9, "Be late");

	private String state;

	private Integer id;

	TypeOfPresence (Integer id, String state) {
		this.state = state;
		this.id = id;
	}

	public static List<TypePresent> getList () {
		List<TypePresent> result = new ArrayList<>();
		for(TypeOfPresence value : TypeOfPresence.values()) {
			result.add(new TypePresent().setId(value.id).setState(value.getState()));
		}
		return result;
	}

	public static List<String> getListState () {
		List<String> result = new ArrayList<>();
		for(TypeOfPresence value : TypeOfPresence.values()) {
			result.add(value.getState());
		}
		return result;
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
