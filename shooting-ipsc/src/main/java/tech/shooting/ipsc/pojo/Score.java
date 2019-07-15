package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.DisqualificationEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Document(collection = "score")
@TypeAlias("score")
@Data
@Accessors(chain = true)
public class Score extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Stage id", required = true)
	@NotNull(message = ValidationConstants.STAGE_ID)
	@Positive(message = ValidationConstants.STAGE_ID_POSITIVE)
	private Long stageId;

	@JsonProperty
	@ApiModelProperty(value = "Person id", required = true)
	@NotNull(message = ValidationConstants.PERSON_ID)
	@Positive(message = ValidationConstants.PERSON_ID_POSITIVE)
	private Long personId;

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
	private DisqualificationEnum disqualificationReason;	
}
