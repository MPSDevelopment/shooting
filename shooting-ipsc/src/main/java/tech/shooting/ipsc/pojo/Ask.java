package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Ask {
	@JsonProperty
	@ApiModelProperty(value = "Question in rus")
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Question in kz")
	private String kz;
}
