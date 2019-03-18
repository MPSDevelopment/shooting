package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "competitor")
@TypeAlias("competitor")
@Data
@Accessors(chain = true)
public class Competitor extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Competitor's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's rfid code")
	private String rfidCode;
	
	@JsonProperty
	@ApiModelProperty(value = "Competitor's number in case we do not have a rfid code")
	private String number;
	
	@JsonProperty
	@ApiModelProperty(value = "Competitor's active, if he passed all the docs and other checks")
	private String active;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Competitor")
	private Person person;

}
