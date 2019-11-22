package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.CommunicationEquipmentEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document(collection = "commequipmenttype")
@TypeAlias("commequipmenttype")
@Data
@Accessors(chain = true)
public class CommunicationEquipmentType extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Communication equipment type name", required = true)
	@NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Communication equipment type", required = true)
	@NotNull(message = ValidationConstants.COMMUNICATION_EQUIPMENT_TYPE_MESSAGE)
	private CommunicationEquipmentEnum type = CommunicationEquipmentEnum.SHORT_WAVE;
}
