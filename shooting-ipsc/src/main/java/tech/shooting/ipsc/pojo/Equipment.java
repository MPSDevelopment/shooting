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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Document(collection = "equipment")
@TypeAlias("equipment")
@Data
@Accessors(chain = true)
public class Equipment extends BaseDocument {

    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Serial number of equipment", required = true)
    @NotBlank(message = ValidationConstants.EQUIPMENT_SERIAL_NUMBER_MESSAGE )
    @Min(value = 7,message = ValidationConstants.EQUIPMENT_SERIAL_NUMBER_MESSAGE)
    private String serialNumber;

    @JsonProperty("equipmentType")
    @ApiModelProperty(value = "Equipment type", required = true)
    @DBRef
    private EquipmentType type;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Person owner;
}