package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.LegendTypeEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document(collection = "legendtype")
@TypeAlias("legendtype")
@Data
@Accessors(chain = true)
public class LegendType extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Legend type name", required = true)
	@NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Legend equipment type", required = true)
	@NotNull(message = ValidationConstants.LEGEND_TYPE_MESSAGE)
	private LegendTypeEnum type = LegendTypeEnum.COMBAT;
}
