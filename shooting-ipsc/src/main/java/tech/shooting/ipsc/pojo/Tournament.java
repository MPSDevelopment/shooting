package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentDivisionEnum;
import tech.shooting.ipsc.enums.TournamentLevelEnum;
import tech.shooting.ipsc.enums.TournamentTypeEnum;

@Document(collection = "tournament")
@TypeAlias("tournament")
@Data
@Accessors(chain = true)
public class Tournament extends BaseDocument {

	public static final String EMAIL_FIELD = "email";

	@JsonProperty
	@ApiModelProperty(value = "Tournament's name", required = true)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's type", required = true)
	private TournamentTypeEnum type;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's date")
	private OffsetDateTime date;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's level", required = true)
	private TournamentLevelEnum level;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's director", required = true)
	private User director;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's main judge", required = true)
	private User mainJudge;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's statistics judge", required = true)
	private User statisticsJudge;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's active ")
	private boolean active = true;
}
