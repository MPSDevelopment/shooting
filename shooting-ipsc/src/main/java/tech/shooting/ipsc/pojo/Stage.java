package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
@Document(collection = "stage")
@TypeAlias("stage")
public class Stage extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Name or № of stage", required = true)
	private String nameOfStage;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC target", required = true)
	private Integer targets;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC popper")
	private Integer popper;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC no shoots target")
	private Integer noShoots;

	@JsonProperty
	@ApiModelProperty(value = "Count of round", required = true)
	private Integer numberOfRoundToBeScored;

	@JsonProperty
	@ApiModelProperty(value = "Count of max points for the stage", required = true)
	private Integer maximumPoints;

	@JsonProperty
	@ApiModelProperty(value = "Description of start position")
	private String startPosition;

	@JsonProperty
	@ApiModelProperty(value = "Description of ready condition")
	private String readyCondition;

	@JsonProperty
	@ApiModelProperty(value = "Description of procedure")
	private String procedure;

	@JsonProperty
	@ApiModelProperty(value = "Counted stage or not")
	private boolean notCounted;

	@JsonProperty
	@ApiModelProperty(value = "Name of fire position")
	private String location;


}
