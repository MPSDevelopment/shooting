package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class StandardConditions {

	@JsonProperty
	@ApiModelProperty(value = "Name conditions by rus", required = true)
	@Pattern(regexp = ValidationConstants.NAME_PATTERN, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
	private String conditionsRus;

	@JsonProperty
	@ApiModelProperty(value = "Name conditions by kz", required = true)
	@Pattern(regexp = ValidationConstants.NAME_PATTERN, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
	private String conditionsKz;

	@JsonProperty
	@ApiModelProperty(value = "Standard coefficient", required = true)
	@Positive(message = ValidationConstants.POSITIVE_MESSAGE)
	private Double coefficient;

	@JsonProperty
	@ApiModelProperty(value = "Standard units", required = true)
	private UnitEnum units;
}
