package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Data
@Accessors(chain = true)
public class WeaponBean {

    @JsonProperty
    @ApiModelProperty(value = "Division", required = true)
    private Long division;

    @JsonProperty
    @ApiModelProperty(value = "Serial number of weapon", required = true)
    @Min(value = 7, message = ValidationConstants.WEAPON_SERIAL_NUMBER_MESSAGE)
    //example AK-74 №4405222
    private String serialNumber;

    @JsonProperty
    @ApiModelProperty(value = "Type of weapon", required = true)
    private Long weaponType;

    @JsonProperty
    @ApiModelProperty(value = "Fired count by weapon")
    @PositiveOrZero(message = ValidationConstants.WEAPON_COUNT_MESSAGE)
    private Integer count;

    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Long owner;
}
