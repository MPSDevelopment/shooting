package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@ApiModel(value = "Division update")
@EqualsAndHashCode(callSuper = false)
@ToString
public class UpdateDivisionBean {
	@JsonProperty
	@ApiModelProperty(value = "Division id", required = true)
	@PositiveOrZero
	private Long id;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	@NotNull(message = ValidationConstants.DIVISION_NAME_MESSAGE)
	@Size(min = 3, max = 20, message = ValidationConstants.DIVISION_NAME_MESSAGE)
	@Pattern(regexp = IpscSettings.NAME_REGEXP, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
	private String name;
}
