package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationCombatElement extends BaseDocument {
	
	@JsonProperty
	@ApiModelProperty(value = "Call sign of the element", required = true)
	private String callSign;
	
	@JsonProperty
	@ApiModelProperty(value = "Commander of the element", required = true)
	private OperationParticipant commander;
	
	@JsonProperty
	@ApiModelProperty(value = "Participant list for the element", required = true)
	private List<OperationParticipant> participants = new ArrayList<>();
	
}
