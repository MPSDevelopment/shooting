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
import javax.validation.constraints.PositiveOrZero;

@Document(collection = "vehicle")
@TypeAlias("vehicle")
@Data
@Accessors(chain = true)
public class Vehicle extends BaseDocument {

    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Serial number of vehicle", required = true)
    @NotBlank(message = ValidationConstants.SERIAL_NUMBER_MESSAGE )
    @Min(value = 7,message = ValidationConstants.SERIAL_NUMBER_MESSAGE)
    private String serialNumber;
    
    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Passport number of vehicle", required = true)
    @Min(value = 7,message = ValidationConstants.PASSPORT_NUMBER_MESSAGE)
    private String passportNumber;
    
    @JsonProperty
    @ApiModelProperty(value = "Fuel count")
    @PositiveOrZero(message = ValidationConstants.WEAPON_COUNT_MESSAGE)
    private Integer count;

    @JsonProperty
    @ApiModelProperty(value = "Vehicle type name", required = true)
    @DBRef
    private VehicleType type;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Person owner;
}