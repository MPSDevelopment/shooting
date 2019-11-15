package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Document(collection = "animal")
@TypeAlias("animal")
@Data
@Accessors(chain = true)
public class Animal extends BaseDocument {

    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Animal name", required = true)
    @NotBlank(message = ValidationConstants.ANIMAL_NAME_MESSAGE )
    @Size(min = 3, max = 30, message = ValidationConstants.ANIMAL_NAME_MESSAGE)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Animal type", required = true)
    @DBRef
    private AnimalType type;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Animal owner")
    private Person owner;
}