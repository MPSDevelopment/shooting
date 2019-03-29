package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

import java.util.List;

@Data
@Accessors(chain = true)
public class Question extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "List of choice", required = true)
	List<Answer> answers;

	@JsonProperty
	@ApiModelProperty(value = "Question", required = true)
	private Ask question;

	@JsonProperty
	@ApiModelProperty(value = "Right choice", required = true)
	private int right;

	@JsonProperty
	@ApiModelProperty(value = "Random write")
	private boolean random;
}
