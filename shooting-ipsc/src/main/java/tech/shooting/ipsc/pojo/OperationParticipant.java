package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
public class OperationParticipant extends BaseDocument {
	
	public static final String PERSON = "person";
	
	@JsonProperty
	@ApiModelProperty(value = "Participant's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Participant active")
	private boolean active;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Participant person")
	private Person person;
	
}
