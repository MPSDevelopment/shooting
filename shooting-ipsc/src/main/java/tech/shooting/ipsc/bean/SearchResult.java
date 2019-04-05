package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Person;

import java.util.Map;

@Data
@Accessors(chain = true)
public class SearchResult {
	@JsonProperty
	Map<String, Long> status;

	@JsonProperty
	private Person person;
}
