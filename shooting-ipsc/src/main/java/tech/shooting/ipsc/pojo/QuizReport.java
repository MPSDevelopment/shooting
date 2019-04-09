package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.serialization.BaseDocumentIdSerializer;

import java.util.List;

@Document("quizreport")
@TypeAlias("quizreport")
@Data
@Accessors(chain = true)
public class QuizReport extends BaseDocument {
	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Person person;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Quiz")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Quiz quiz;

	@JsonProperty
	@ApiModelProperty(value = "Mark")
	private int mark;

	@JsonProperty
	@ApiModelProperty(value = "Incorrect answer")
	private List<Row> incorrect;

	@JsonProperty
	@ApiModelProperty(value = "Skip answer")
	private List<Ask> skip;
}
