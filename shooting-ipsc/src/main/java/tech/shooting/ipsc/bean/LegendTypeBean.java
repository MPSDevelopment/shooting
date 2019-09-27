package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.LegendTypeEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class LegendTypeBean {

	@JsonProperty
	@ApiModelProperty(value = "Legend type name", required = true)
	@Size(min = 2, message = "Must be min 2 characters")
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Legend type", required = true)
	@NotNull(message = ValidationConstants.EQUIPMENT_TYPE_MESSAGE)
	private LegendTypeEnum type = LegendTypeEnum.COMMUNICATION;
}
