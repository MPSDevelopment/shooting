package tech.shooting.ipsc.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.mongo.BaseDocument;

@Getter
@Setter
public class OperationRoute extends BaseDocument {
	
	@JsonProperty
	@ApiModelProperty(value = "Route object id")
	private Long objectId;
	
	@JsonProperty
	@ApiModelProperty(value = "Waypoint list")
	private List<Waypoint> waypoints = new ArrayList<>();

}
