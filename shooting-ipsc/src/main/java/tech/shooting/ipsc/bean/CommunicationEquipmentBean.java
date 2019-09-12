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
public class CommunicationEquipmentBean {

    @JsonProperty
    @ApiModelProperty(value = "Serial number of equipment", required = true)
    @Min(value = 7, message = ValidationConstants.COMMUNICATION_EQUIPMENT_SERIAL_NUMBER_MESSAGE)
    private String serialNumber;

    @JsonProperty
    @ApiModelProperty(value = "Type of equipment", required = true)
    private Long type;

    @JsonProperty
    @ApiModelProperty(value = "Owner equipment")
    private Long owner;
}
