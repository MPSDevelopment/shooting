package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.TypeOfPresence;

@Data
@Accessors(chain = true)
public class Stat {
	
	@JsonProperty
	private TypeOfPresence status;

	@JsonProperty
	private Integer count;

	@Override
	public String toString () {
		return status + " : " + count;
	}
}
