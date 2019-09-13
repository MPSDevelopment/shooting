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
import javax.validation.constraints.PositiveOrZero;

@Document(collection = "weapontype")
@TypeAlias("weapontype")
@Data
@Accessors(chain = true)
public class WeaponType extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Weapon type name", required = true)
    @NotBlank(message = ValidationConstants.WEAPON_TYPE_NAME_MESSAGE)
    private String name;
    
    @JsonProperty
    @ApiModelProperty(value = "Weapon type's ammo usual count")
    @PositiveOrZero(message = ValidationConstants.WEAPON_TYPE_AMMO_COUNT_MESSAGE)
    private Integer ammoCount;
}
