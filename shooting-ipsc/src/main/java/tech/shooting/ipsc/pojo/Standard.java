package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Accessors(chain = true)
@Document(collection = "standard")
@TypeAlias("standard")
@ToString(callSuper = true)
public class Standard extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Standard info", required = true)
	private Info info;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Subject", required = true)
	private Subject subject;

	@JsonProperty
	@ApiModelProperty(value = "Is active standard", required = true)
	private boolean active;

	@JsonProperty
	@ApiModelProperty(value = "Is group standard", required = true)
	private boolean groups;

	@JsonProperty
	@ApiModelProperty(value = "Is running standard", required = true)
	private boolean running;
	
	@JsonProperty
	@ApiModelProperty(value = "Number of laps to finish the standard", required = true)
	@PositiveOrZero
	private Integer laps;

	@JsonProperty
	@ApiModelProperty(value = "List categories by time")
	private List<CategoryByTime> categoryByTimeList = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "List categories by points")
	private List<CategoryByPoints> categoryByPointsList = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "List fails")
	private List<StandardFails> failsList;

	@JsonProperty
	@ApiModelProperty(value = "List conditions")
	private List<StandardConditions> conditionsList;

}
