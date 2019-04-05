package tech.shooting.ipsc.bean;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.TypeOfPresence;

@Data
@Accessors(chain = true)
public class Stat {
	private TypeOfPresence status;

	private Integer count;

	@Override
	public String toString () {
		return status + " : " + count;
	}
}
