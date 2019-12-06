package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tag {

	@JsonProperty
	private String code;

	@JsonProperty
	private long firstSeenTime;

	@JsonProperty
	private long lastSeenTime;

}
