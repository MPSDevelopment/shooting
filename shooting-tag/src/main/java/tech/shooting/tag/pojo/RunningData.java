package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RunningData {

	@JsonProperty
	private String code;

//	@JsonProperty
//	private Long personId;
//
//	@JsonProperty
//	private String personName;

	@JsonProperty
	private long firstTime;

	@JsonProperty
	private long lastTime;

	@JsonProperty
	private int laps = 0;
}
