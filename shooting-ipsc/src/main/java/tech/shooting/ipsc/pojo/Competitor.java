package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "competitor")
@TypeAlias("competitor")
@Data
@Accessors(chain = true)
public class Competitor extends BaseDocument {
	
	public static final String PERSON = "person";
	
	@JsonProperty("userName")
	@ApiModelProperty(value = "Competitor's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's rfid code")
	private String rfidCode;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's number code")
	private String number;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's active, if he passed all the docs and other checks")
	private boolean active;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Competitor")
	private Person person;

	@JsonProperty
	@ApiModelProperty(value = "List of score")
	private List<Score> result = new ArrayList<>();
}
