package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class QuizName {
	@JsonProperty
	@ApiModelProperty(value = "Quiz name in rus", required = true)
	@NotBlank
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name in kz", required = true)
	@NotBlank
	private String kz;
}