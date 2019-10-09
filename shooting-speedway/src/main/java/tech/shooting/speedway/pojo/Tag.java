package tech.shooting.speedway.pojo;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
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
