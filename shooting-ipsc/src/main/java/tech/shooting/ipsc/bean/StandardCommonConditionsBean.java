package tech.shooting.ipsc.bean;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

@Data
@Accessors(chain = true)
public class StandardCommonConditionsBean {
    @JsonProperty
    @ApiModelProperty(value = "Name conditions by rus", required = true)
    @Pattern(regexp = IpscSettings.NAME_REGEXP, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
    private String conditionsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by kz", required = true)
    @Pattern(regexp = IpscSettings.NAME_REGEXP, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
    private String conditionsKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
    @Positive(message = ValidationConstants.POSITIVE_MESSAGE)
    private Double coefficient;

    @JsonProperty
    @ApiModelProperty(value = "Unit id", required = true)
    private UnitEnum units;
}
