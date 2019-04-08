package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DivisionDropList {
	@JsonProperty
	@ApiModelProperty(value = "Division id", required = true)
	String id;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	String name;
}
