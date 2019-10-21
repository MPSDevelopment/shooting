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

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Document(collection = "standard")
@TypeAlias("standard")
@ToString(callSuper = true)
public class Standard extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Info from standard", required = true)
	private Info info;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Subject standard", required = true)
	private Subject subject;

	@JsonProperty
	@ApiModelProperty(value = "Is active", required = true)
	private boolean active;

	@JsonProperty
	@ApiModelProperty(value = "Is groups", required = true)
	private boolean groups;

	@JsonProperty
	@ApiModelProperty(value = "List categories by time", required = true)
	private List<CategoryByTime> categoriesList;
	
	@JsonProperty
	@ApiModelProperty(value = "List categories by points", required = true)
	private List<CategoryByPoints> categoryList;

	@JsonProperty
	@ApiModelProperty(value = "List fails")
	private List<StandardFails> failsList;

	@JsonProperty
	@ApiModelProperty(value = "List conditions")
	private List<StandardConditions> conditionsList;

}
