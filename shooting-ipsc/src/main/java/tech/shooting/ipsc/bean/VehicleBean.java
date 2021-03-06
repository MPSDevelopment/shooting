package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Accessors(chain = true)
public class VehicleBean {
	
	@Id
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected Long id;

    @JsonProperty
    @ApiModelProperty(value = "Serial number of vehicle", required = true)
    @Size(min = 3, max = 30, message = ValidationConstants.SERIAL_NUMBER_MESSAGE)
    private String serialNumber;
    
    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Passport number of vehicle", required = true)
    @Size(min = 3, max = 30, message = ValidationConstants.PASSPORT_NUMBER_MESSAGE)
    private String passportNumber;
    
    @JsonProperty
    @ApiModelProperty(value = "Fuel count")
    @PositiveOrZero(message = ValidationConstants.WEAPON_COUNT_MESSAGE)
    private Integer count;

    @JsonProperty
    @ApiModelProperty(value = "Type of vehicle", required = true)
    @NotNull(message = ValidationConstants.NOT_NULL_MESSAGE)
    private Long type;

    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Long owner;
}
