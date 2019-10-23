package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Ask;
import tech.shooting.ipsc.pojo.Row;

import java.util.List;

@Data
@Accessors(chain = true)
public class QuizReportBean {
	@JsonProperty
	@ApiModelProperty(value = "Person")
	private Long person;

	@JsonProperty
	@ApiModelProperty(value = "Quiz")
	private Long quiz;

	@JsonProperty
	@ApiModelProperty(value = "Mark")
	private int score;

	@JsonProperty
	@ApiModelProperty(value = "Incorrect answer")
	private int incorrect;

	@JsonProperty
	@ApiModelProperty(value = "Skip answer")
	private int skip;
}
