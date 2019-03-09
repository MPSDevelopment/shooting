package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentCategoryEnum;

@Document(collection = "competitor")
@TypeAlias("competitor")
@Data
@Accessors(chain = true)
public class Competitor extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Competitor's name", required = true)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Competitor's rfid code", required = true)
	@Indexed(unique = true)
	private String rfidCode;
	
	@DBRef
	private Person person;

}
