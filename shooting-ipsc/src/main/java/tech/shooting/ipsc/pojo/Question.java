package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class Question extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "List of choice", required = true)
	@Size(min = 2, max = 4, message = ValidationConstants.ANSWERS_SIZE_MESSAGE)
	List<Answer> answers;

	@JsonProperty
	@ApiModelProperty(value = "Question", required = true)
	private Ask question;

	@JsonProperty
	@ApiModelProperty(value = "Right choice", required = true)
	@PositiveOrZero
	private int right;

	@JsonProperty
	@ApiModelProperty(value = "Random write")
	private boolean random = true;

	@JsonProperty
	@ApiModelProperty(value = "Is active")
	private boolean active = true;
}
