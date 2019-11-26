package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationSignal extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Signal's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Signal's value", required = true)
	private String value;
}
