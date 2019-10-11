package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tag {

	@JsonProperty
	private short crc;

	@JsonProperty
	private long firstSeenTime;

	@JsonProperty
	private long lastSeenTime;

}
