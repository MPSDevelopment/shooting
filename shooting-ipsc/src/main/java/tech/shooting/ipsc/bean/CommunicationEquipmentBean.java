package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

@Data
@Accessors(chain = true)
public class CommunicationEquipmentBean {
	
	@Id
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected Long id;

    @JsonProperty
    @ApiModelProperty(value = "Serial number of equipment", required = true)
    @Size(min = 3, max = 30, message = ValidationConstants.COMMUNICATION_EQUIPMENT_SERIAL_NUMBER_MESSAGE)
    private String serialNumber;

    @JsonProperty
    @ApiModelProperty(value = "Type of equipment", required = true)
    private Long type;

    @JsonProperty
    @ApiModelProperty(value = "Owner equipment")
    private Long owner;
}
