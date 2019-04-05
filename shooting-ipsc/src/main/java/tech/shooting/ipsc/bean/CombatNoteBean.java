package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class CombatNoteBean {
	@JsonProperty
	@ApiModelProperty(value = "Filling date")
	@NotNull
	private OffsetDateTime date;

	@JsonProperty
	@ApiModelProperty(value = "Responsibility person id")
	private Long combateId;
}
