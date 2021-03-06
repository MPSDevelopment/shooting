package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class WeaponBean {
	
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
//	@NotNull(message = ValidationConstants.NOT_NULL_MESSAGE)
	protected Long id;

    @JsonProperty
    @ApiModelProperty(value = "Serial number of weapon", required = true)
    @Size(min = 3, max = 30, message = ValidationConstants.SERIAL_NUMBER_MESSAGE)
    //example AK-74 №4405222
    private String serialNumber;

    @JsonProperty
    @ApiModelProperty(value = "Type of weapon", required = true)
    @NotNull(message = ValidationConstants.NOT_NULL_MESSAGE)
    private Long weaponType;

    @JsonProperty
    @ApiModelProperty(value = "Fired count by weapon")
    @PositiveOrZero(message = ValidationConstants.WEAPON_COUNT_MESSAGE)
    private Integer count;

    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Long owner;
}
