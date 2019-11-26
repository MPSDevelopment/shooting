package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationMainIndicator extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Military base", required = true)
	private String militaryBase;

	@JsonProperty
	@ApiModelProperty(value = "Route", required = true)
	private String route;

	@JsonProperty
	@ApiModelProperty(value = "Route distance (km)", required = true)
	private Float distance;

	@JsonProperty
	@ApiModelProperty(value = "Route time (hours)", required = true)
	private Float time;
}
