package tech.shooting.ipsc.bean;

import lombok.Data;
import tech.shooting.ipsc.enums.TypeOfPresence;

@Data
public class Stat {
	private TypeOfPresence status;

	private Integer count;

	@Override
	public String toString () {
		return status + " : " + count;
	}
}
