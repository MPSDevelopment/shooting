package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import java.time.OffsetDateTime;
import java.util.List;

@Document(collection = "quizscore")
@TypeAlias("quizscore")
@Data
@Accessors(chain = true)
public class QuizScore extends BaseDocument {
	
	public static final String QUIZ_FIELD = "quizId";
	
	public static final String PERSON_FIELD = "personId";
	
	public static final String TIME_FIELD = "datetime";
	
//	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person")
//	@JsonSerialize(using = BaseDocumentIdSerializer.class)
//	@JsonDeserialize(using = PersonFromIdDeserializer.class)
	private Long personId;

//	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Quiz")
//	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Long quizId;

	@JsonProperty
	@ApiModelProperty(value = "Mark")
	private int score;

	@JsonProperty
	@ApiModelProperty(value = "Incorrect answer")
	private List<Row> incorrect;

	@JsonProperty
	@ApiModelProperty(value = "Skip answer")
	private List<Ask> skip;
	
	@JsonProperty
	@ApiModelProperty(value = "Score's datetime")
	private OffsetDateTime datetime = OffsetDateTime.now();
}
