package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
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
@ToString(callSuper = true)
public class CheckIn extends BaseDocument {
	public static final String DIVISION_ID = "divisionId";

	public static final String STATUS = "status";

	public static final String PERSON = "person";

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person id from db")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Person person;

	@JsonProperty
	@ApiModelProperty(value = "Status is present")
	private TypeOfPresence status;

	@JsonProperty
	@ApiModelProperty(value = "Division id")
	private Long divisionId;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Inspection officer")
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private User officer;
}
