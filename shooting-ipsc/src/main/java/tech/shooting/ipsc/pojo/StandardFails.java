package tech.shooting.ipsc.pojo;

import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class StandardFails {

	@JsonProperty
	@ApiModelProperty(value = "Name fail by rus", required = true)
	private String nameFailsRus;

	@JsonProperty
	@ApiModelProperty(value = "Name fail by kz", required = true)
	private String nameFailsKz;

	@JsonProperty
	@ApiModelProperty(value = "Minus point for this fail", required = true)
	@Positive(message = ValidationConstants.POSITIVE_MESSAGE)
	private Long minusPoint;
}
