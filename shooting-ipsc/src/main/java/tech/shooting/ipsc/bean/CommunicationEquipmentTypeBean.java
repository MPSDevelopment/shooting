package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.CommunicationEquipmentEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class CommunicationEquipmentTypeBean {
	
    @JsonProperty
    @ApiModelProperty(value = "Communication equipment type name", required = true)
    @Size(min = 2, message = "Must be min 2 characters")
    private String name;
    
	@JsonProperty
	@ApiModelProperty(value = "Communication equipment type", required = true)
	@NotNull(message = ValidationConstants.COMMUNICATION_EQUIPMENT_TYPE_MESSAGE)
	private CommunicationEquipmentEnum type = CommunicationEquipmentEnum.SHORT_WAVE;
}
