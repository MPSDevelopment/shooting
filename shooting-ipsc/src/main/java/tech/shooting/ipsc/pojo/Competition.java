package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;

import java.time.OffsetDateTime;

@Data
@Document(collection = "competition")
@TypeAlias("competition")
@Accessors(chain = true)
public class Competition extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Competition name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Competition date", required = true)
	private OffsetDateTime competitionDate;

	@JsonProperty
	@ApiModelProperty(value = "Competition qualifier rank", notes = "For whom the competition is held")
	private String qualifierRank;

	@JsonProperty
	@ApiModelProperty(value = "Competition type weapon", required = true)
	private ExerciseWeaponTypeEnum typeWeapon;

	@JsonProperty
	@ApiModelProperty(value = "Competition rank", notes = "Level Competition")
	private String rank;

	@JsonProperty
	@ApiModelProperty(value = "Competition location", notes = "Competition location")
	private String location;

	@JsonProperty
	@ApiModelProperty(value = "Competition active ")
	private boolean active = true;

	@JsonProperty
	@ApiModelProperty(value = "Competition's Match Director", notes = "Competition's Match Director ")
	private String matchDirector;

	@JsonProperty
	@ApiModelProperty(value = "Competition's Range Master", notes = "Competition's Range Master")
	private String rangeMaster;

	@JsonProperty
	@ApiModelProperty(value = "Competition's Stats Officer ", notes = "Competition's Stats Officer ")
	private String statsOfficer;

}
