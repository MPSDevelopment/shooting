package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Question;
import tech.shooting.ipsc.pojo.QuizName;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Quiz create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class QuizBean {
	@JsonProperty
	@ApiModelProperty(value = "Subject quiz", required = true)
	@NotNull
	private Subject subject;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name", required = true)
	@NotNull
	private QuizName name;

	@JsonProperty
	@ApiModelProperty(value = "List of question's")
	private List<Question> questionList = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Time for answer", required = true)
	@Positive(message = ValidationConstants.TIME_MESSAGE)
	private Long time;

	@JsonProperty
	@ApiModelProperty(value = "% for satisfactorily mark", required = true)
	@PositiveOrZero
	private int satisfactorily;

	@JsonProperty
	@ApiModelProperty(value = "% for great mark", required = true)
	@PositiveOrZero
	private int great;

	@JsonProperty
	@ApiModelProperty(value = "% for good mark", required = true)
	@PositiveOrZero
	private int good;
}
