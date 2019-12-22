package tech.shooting.ipsc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

public enum StandardPassEnum {
	EXCELLENT, GOOD, SATISFACTORY, UNSATISFACTORY;

	@JsonCreator
	public static StandardPassEnum fromStringOperator(String stringValue) {
		if (stringValue != null) {
			for (StandardPassEnum item : StandardPassEnum.values()) {
				if (stringValue.equalsIgnoreCase(item.toString())) {
					return item;
				}
			}
		}
		return UNSATISFACTORY;
	}

}
