package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationCommandantService extends BaseDocument {
	
	@JsonProperty
	@ApiModelProperty(value = "Number of the district", required = true)
	private String districtNumber;
	
	@JsonProperty
	@ApiModelProperty(value = "Commandant of the district", required = true)
	private OperationParticipant commandant;
	
	@JsonProperty
	@ApiModelProperty(value = "Distance of the district", required = true)
	private Float distance;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of posts", required = true)
	private int postNumber;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of officers", required = true)
	private int officersNumber;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of soldiers", required = true)
	private int soldiersNumber;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of communication equipment", required = true)
	private int communicationEquipmentNumber;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of transport vehicles", required = true)
	private int transportNumber;
	
	
}
