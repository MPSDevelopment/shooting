package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.serialization.BaseDocumentIdSerializer;

@Document("checkin")
@TypeAlias("checkin")
@Data
@Accessors(chain = true)
public class CheckIn extends BaseDocument {
	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person id from db")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Person person;

	@JsonProperty
	@ApiModelProperty(value = "Status is present")
	private TypeOfPresence status;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Inspection officer")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private User officer;
}
