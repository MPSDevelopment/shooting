package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationSymbol extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Symbol's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Symbol's image path", required = true)
	private String imagePath;
}
