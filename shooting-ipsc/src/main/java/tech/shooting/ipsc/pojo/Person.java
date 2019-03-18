package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;

import java.time.OffsetDateTime;
import java.util.List;


@Document(collection = "person")
@TypeAlias("person")
@Data
@Accessors(chain = true)
public class Person extends BaseDocument {

	public static final String NAME_AND_IPSC_FIELD = "name and ipsc code";

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

	@JsonProperty
	@ApiModelProperty(value = "Person's team")
	private String team;

	@JsonProperty
	@ApiModelProperty(value = "Person's rank")
	private String rank;
	
	@JsonProperty
	@ApiModelProperty(value = "Person's IPSC codes")
	private List<WeaponIpscCode> codes;

	@JsonProperty("level")
	@ApiModelProperty(value = "Person's qualifier rank")
	private ClassificationBreaks qualifierRank;
}
