package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.TypePresent;

import java.util.ArrayList;
import java.util.List;

public enum TypeOfPresence {
	ALL("ALL"), 
	UNDEFINED("UNDEFINED"),
	MISSION("MISSION"),
	SICK_LEAVE("SICK_LEAVE"),
	INFIRMARY("INFIRMARY"),
	HOSPITAL("HOSPITAL"),
	OUTFIT("OUTFIT"),
	DAY_OFF("DAY_OFF"),
	PRESENT("PRESENT"),
	DELAY("DELAY");

	private String state;

	TypeOfPresence () {
	}

	TypeOfPresence (String state) {
		this.state = state;
	}

	public static List<TypePresent> getList () {
		List<TypePresent> result = new ArrayList<>();
		for(TypeOfPresence value : TypeOfPresence.values()) {
			result.add(new TypePresent().setState(value.getState()));
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

	public static TypeOfPresence getByState (String state) {
		for(TypeOfPresence value : TypeOfPresence.values()) {
			if(value.getState().equals(state)) {
				return value;
			}
		}
		return null;
	}

	@Override
	public String toString () {
		return String.valueOf(state);
	}

	public String getState () {
		return state;
	}
}
