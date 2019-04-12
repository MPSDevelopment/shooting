package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Answer {

	@JsonProperty
	@ApiModelProperty(value = "Answer number", required = true)
	@NotNull
	@Min(value = 1, message = ValidationConstants.NUMBER_MIN_MESSAGE)
	@Max(value = 4, message = ValidationConstants.NUMBER_MAX_MESSAGE)
	private Integer number;

	@JsonProperty
	@ApiModelProperty(value = "Answer in rus", required = true)
	@NotBlank
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Answer in kz", required = true)
	@NotBlank
	private String kz;
	
	@JsonProperty
	@ApiModelProperty(value = "Question image path", required = true)
	private String imagePath;
}