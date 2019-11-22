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

@Document(collection = "vehicletype")
@TypeAlias("vehicletype")
@Data
@Accessors(chain = true)
public class VehicleType extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Vehicle type name", required = true)
	@NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
	private String name;
}
