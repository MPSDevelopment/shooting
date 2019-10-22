package tech.shooting.ipsc.bean;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class QuizScoreRequest {

	@JsonProperty
	@ApiModelProperty(value = "Subject id")
	private Long subjectId;
	
	@JsonProperty
	@ApiModelProperty(value = "Quiz id")
	private Long quizId;
	
	@JsonProperty
	@ApiModelProperty(value = "Person id")
	private Long personId;
	
	@JsonProperty
	@ApiModelProperty(value = "Division id")
	private Long divisionId;
	
	@JsonProperty
	@ApiModelProperty(value = "Start date")
	private OffsetDateTime startDate;
	
	@JsonProperty
	@ApiModelProperty(value = "End date")
	private OffsetDateTime endDate;

}
