package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperationCombatListHeaderBean {

	@JsonProperty
	@ApiModelProperty(value = "type")
	private String type;

	@JsonProperty
	@ApiModelProperty(value = "subtype")
	private String subtype;

	@JsonProperty
	@ApiModelProperty(value = "name")
	private String name;
	
	

}
