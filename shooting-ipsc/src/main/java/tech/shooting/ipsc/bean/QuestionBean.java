package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.pojo.Answer;
import tech.shooting.ipsc.pojo.Ask;

import java.util.List;

@Data
@Accessors(chain = true)
public class QuestionBean extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "List of choice", required = true)
	List<Answer> answers;

	@JsonProperty
	@ApiModelProperty(value = "Question", required = true)
	private Ask question;

	@JsonProperty
	@ApiModelProperty(value = "Random write")
	private boolean random;
}
