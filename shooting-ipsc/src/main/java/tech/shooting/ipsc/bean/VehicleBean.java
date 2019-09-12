package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Accessors(chain = true)
public class VehicleBean {

    @JsonProperty
    @ApiModelProperty(value = "Serial number of vehicle", required = true)
    @Min(value = 7, message = ValidationConstants.VEHICLE_SERIAL_NUMBER_MESSAGE)
    private String serialNumber;
    
    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Passport number of vehicle", required = true)
    private String passportNumber;
    
    @JsonProperty
    @ApiModelProperty(value = "Fuel count")
    @PositiveOrZero(message = ValidationConstants.WEAPON_COUNT_MESSAGE)
    private Integer count;

    @JsonProperty
    @ApiModelProperty(value = "Type of vehicle", required = true)
    private Long type;

    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Long owner;
}
