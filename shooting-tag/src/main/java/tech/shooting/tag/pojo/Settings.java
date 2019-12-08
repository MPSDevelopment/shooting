package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Settings {

	public static final String NAME_FIELD = "name";

	@JsonProperty
	private String name;

	@JsonProperty(value = "runIp")
	private String tagServiceIp;
}
