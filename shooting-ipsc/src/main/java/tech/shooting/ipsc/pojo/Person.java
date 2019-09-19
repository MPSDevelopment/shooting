package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.ClassificationBreaks;

import java.time.OffsetDateTime;

@Document(collection = "person")
@TypeAlias("person")
@Data
@Accessors(chain = true)
public class Person extends BaseDocument {
	
	public static final String NAME_AND_BIRTHDAY = "name and birthday";

	public static final String DIVISION = "division";

	public static final String RANK = "rank";
	
	public static final String CALL = "call";

	@JsonProperty("userName")
	@ApiModelProperty(value = "Person's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Person's birthday")
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "Person's active ")
	private boolean active = true;

	@JsonProperty
	@ApiModelProperty(value = "Person's address", required = true)
	private Address address;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person's rank")
	private Rank rank;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person's division")
	private Division division;
	
	private Long divisionId;

	@JsonProperty("level")
	@ApiModelProperty(value = "Person's qualifier rank")
	private ClassificationBreaks qualifierRank;
	
	@JsonProperty
	@ApiModelProperty(value = "Person's rfid code")
	private String rfidCode;

	@JsonProperty
	@ApiModelProperty(value = "Person's number code")
	private String number;
	
	@JsonProperty
	@ApiModelProperty(value = "Person's call")
	private String call;

	public Person setDivision(Division division) {
		this.division = division;
		divisionId = division == null ? null : division.getId();
		return this;
	}
}
