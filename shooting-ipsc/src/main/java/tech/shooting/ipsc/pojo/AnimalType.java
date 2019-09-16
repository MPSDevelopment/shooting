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

@Document(collection = "animaltype")
@TypeAlias("animaltype")
@Data
@Accessors(chain = true)
public class AnimalType extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Animal type name", required = true)
    @NotBlank(message = ValidationConstants.WEAPON_TYPE_NAME_MESSAGE)
    private String name;
}
