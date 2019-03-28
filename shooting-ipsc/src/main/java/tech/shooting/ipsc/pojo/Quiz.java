package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.Subject;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "quiz")
@TypeAlias("quiz")
@Data
@Accessors(chain = true)
public class Quiz extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "Subject quiz")
	private Subject subject;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name")
	private QuizName name;

	@JsonProperty
	@ApiModelProperty(value = "List of question's")
	private List<Question> questionList = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Time for answer")
	private Long time;

	@JsonProperty
	@ApiModelProperty(value = "% for satisfactorily mark")
	private byte satisfactorily;

	@JsonProperty
	@ApiModelProperty(value = "% for great mark")
	private byte great;

	@JsonProperty
	@ApiModelProperty(value = "% for good mark")
	private byte good;
}