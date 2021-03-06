package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class Ask {
	@JsonProperty
	@ApiModelProperty(value = "Question in rus", required = true)
	@NotBlank
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Question in kz", required = true)
	@NotBlank
	private String kz;
	
	@JsonProperty
	@ApiModelProperty(value = "Question image path", required = true)
	private String imagePath;
}
