package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class QuizScoreBean {
	@JsonProperty
	@ApiModelProperty(value = "Quiz id")
	@NotNull(message = ValidationConstants.QUIZ_ID_MESSAGE)
	private long quizId;

	@JsonProperty
	@ApiModelProperty(value = "Question and Answer")
	private List<RowBean> list;

	@JsonProperty
	@ApiModelProperty(value = "Person id from db")
	@NotNull(message = ValidationConstants.PERSON_ID)
	private Long person;
}
