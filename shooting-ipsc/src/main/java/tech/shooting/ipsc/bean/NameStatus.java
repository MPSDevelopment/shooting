package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Person;

import java.time.OffsetDateTime;

@Data
@Accessors (chain = true)
public class NameStatus {
	@JsonProperty
	String status;

	@JsonProperty
	OffsetDateTime date;

	@JsonProperty
	private Person person;
}
