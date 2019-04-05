package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class QuizName {
	@JsonProperty
	@ApiModelProperty(value = "Quiz name in rus", required = true)
	@NotBlank(message = ValidationConstants.QUIZ_RUS_MESSAGE)
	@Size(min = 3, message = ValidationConstants.QUIZ_RUS_MESSAGE)
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name in kz", required = true)
	@NotBlank(message = ValidationConstants.QUIZ_KZ_MESSAGE)
	@Size(min = 3, message = ValidationConstants.QUIZ_KZ_MESSAGE)
	private String kz;
}