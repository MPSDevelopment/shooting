package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperationSignal {

	@JsonProperty
	@ApiModelProperty(value = "Signal's name", required = true)
	private String name;
	
    @JsonProperty
    @ApiModelProperty(value = "Signal's value", required = true)
    private String value;
}
