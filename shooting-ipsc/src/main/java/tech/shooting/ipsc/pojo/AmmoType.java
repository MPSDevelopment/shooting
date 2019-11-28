package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Document(collection = "ammotype")
@TypeAlias("ammotype")
@Data
@Accessors(chain = true)
public class AmmoType extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Ammunition type name", required = true)
    @NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
    private String name;
    
    @JsonProperty
    @ApiModelProperty(value = "Weapon type", required = true)
    @NotNull(message = ValidationConstants.AMMO_TYPE_WEAPON_TYPE_MESSAGE)
    private WeaponType ammunitionWeaponType;
    
    @JsonProperty
    @ApiModelProperty(value = "Weapon type's ammo usual count")
    @PositiveOrZero(message = ValidationConstants.AMMO_TYPE_COUNT_MESSAGE)
    private Integer count;
}
