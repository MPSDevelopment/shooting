package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "quiz")
@TypeAlias("quiz")
@Accessors(chain = true)
public class Quiz extends BaseDocument {
	
	public static final String QUESTIONS = "questionList";

	public static final String SUBJECT = "subject";

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Subject quiz", required = true)
	private Subject subject;

	@JsonProperty
	@ApiModelProperty(value = "Status")
	private boolean active;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name", required = true)
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
