package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class WeaponTypeBean {
    @JsonProperty
    @ApiModelProperty(value = "Weapon type name", required = true)
    @Size(min = 2, message = "Must be min 2 characters")
    private String name;
    
    @JsonProperty
    @ApiModelProperty(value = "Weapon type's ammo usual count")
    @PositiveOrZero(message = ValidationConstants.WEAPON_TYPE_AMMO_COUNT_MESSAGE)
    private Integer ammoCount;
}
