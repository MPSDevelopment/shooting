package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DisqualificationBean {
	@JsonProperty
	@ApiModelProperty(value = "Checked field")
	private boolean checked;

	@JsonProperty
	@ApiModelProperty(value = "Type of disqualification field")
	private String name;
}
