package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;

import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationCombatElementBean extends BaseDocument {
	
	@JsonProperty
	@ApiModelProperty(value = "Name of the element", required = true)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Call sign of the element", required = true)
	private String callSign;
	
	@JsonProperty
	@ApiModelProperty(value = "Commander of the element", required = true)
	private Long commander;
	
	@JsonProperty
	@ApiModelProperty(value = "Participant list for the element", required = true)
	private List<Long> participants = new ArrayList<>();
	
}
