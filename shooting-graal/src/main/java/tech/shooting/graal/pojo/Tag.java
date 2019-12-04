package tech.shooting.graal.pojo;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
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
