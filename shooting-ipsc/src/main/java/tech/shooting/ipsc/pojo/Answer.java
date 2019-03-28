package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Answer {
	@JsonProperty
	@ApiModelProperty(value = "Answer in rus")
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Answer in kz")
	private String kz;
}