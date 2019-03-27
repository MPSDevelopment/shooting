package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;
import tech.shooting.ipsc.enums.TypeMarkEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
@ApiModel("Create score")
public class ScoreBean implements ValiationExportable {
	@JsonProperty
	@ApiModelProperty(value = "Competitor's rfid  or number code", required = true)
	private TypeMarkEnum type;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's number in case we do not have a rfid code or rfid code", required = true)
	@NotBlank
	private String mark;

	@JsonProperty
	@ApiModelProperty(value = "Person score of this stage", required = true)
	@NotNull(message = ValidationConstants.SCORE_MESSAGE)
	private Integer score;

	@JsonProperty
	@ApiModelProperty(value = "Person time of executing this stage", required = true)
	@NotNull(message = ValidationConstants.TIME_MESSAGE)
	@Positive(message = ValidationConstants.TIME_MESSAGE)
	private Long timeOfExercise;

	@JsonProperty
	@ApiModelProperty(value = "Disqualification description")
	private String disqualificationReason;
}
