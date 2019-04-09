package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class RowBean {
	@JsonProperty
	@ApiModelProperty(value = "Question id")
	@NotNull(message = ValidationConstants.QUESTION_ID_MESSAGE)
	private Long questionId;

	@JsonProperty
	@ApiModelProperty(value = "Answer")
	private Long answer;
}
