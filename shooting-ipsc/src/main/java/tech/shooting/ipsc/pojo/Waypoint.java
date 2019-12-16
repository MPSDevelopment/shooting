package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.mongo.BaseDocument;

@Getter
@Setter
public class Waypoint extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Waypoint's number")
	protected Integer number;

	@JsonProperty("lat")
	@ApiModelProperty(value = "Waypoint's latitude")
	protected double latitude;

	@JsonProperty("lng")
	@ApiModelProperty(value = "Waypoint's longitude")
	protected double longitude;

	@JsonProperty
	@ApiModelProperty(value = "RoutePoint's label")
	protected String label;

	@JsonProperty
	@ApiModelProperty(value = "WayPoint's height")
	private Double height;
}
