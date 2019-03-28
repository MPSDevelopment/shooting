package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QuizName {
	@JsonProperty
	@ApiModelProperty(value = "Quiz name in rus")
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Quiz name in kz")
	private String kz;
}