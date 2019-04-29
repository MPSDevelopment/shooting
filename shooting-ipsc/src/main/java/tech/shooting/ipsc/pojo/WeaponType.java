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

@Document(collection = "weapontype")
@TypeAlias("weapontype")
@Data
@Accessors(chain = true)
public class WeaponType extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Weapon name", required = true)
    @NotBlank(message = ValidationConstants.WEAPON_NAME_MESSAGE)
    private String name;
}