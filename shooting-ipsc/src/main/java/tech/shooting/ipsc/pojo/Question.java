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
	@ApiModelProperty(value = "List of choice")
	List<Answer> answers;

	@JsonProperty
	@ApiModelProperty(value = "Question")
	private Ask question;

	@JsonProperty
	@ApiModelProperty(value = "Right choice")
	private byte right;

	@JsonProperty
	@ApiModelProperty(value = "Random write")
	private boolean random;
}
