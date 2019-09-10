package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class VehicleTypeBean {
	@JsonProperty
	@ApiModelProperty(value = "Vehicle type name", required = true)
	@Size(min = 2, message = "Must be min 2 characters")
	private String name;
}
