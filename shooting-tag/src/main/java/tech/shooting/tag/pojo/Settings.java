package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Settings extends BaseDocument {

	public static final String NAME_FIELD = "name";

	@JsonProperty
	private String name;

	@JsonProperty(value = "runIp")
	private String tagServiceIp;
}
