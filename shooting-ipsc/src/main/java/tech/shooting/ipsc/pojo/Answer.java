package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class Answer implements ValiationExportable {

	@JsonProperty
	@ApiModelProperty(value = "Answer number", required = true)
	private Integer number;

	@JsonProperty
	@ApiModelProperty(value = "Answer in rus", required = true)
	@NotBlank
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Answer in kz", required = true)
	@NotBlank
	private String kz;
}