package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Competition create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class CreateCompetition {


	@JsonProperty
	@ApiModelProperty(value = "Competition name", required = true)
	@NotNull(message = ValidationConstants.COMPETITION_NAME_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Competition date", required = true)
	private OffsetDateTime competitionDate;

	@JsonProperty
	@ApiModelProperty(value = "Competition qualifier rank", notes = "For whom the competition is held")
	private ClassificationBreaks qualifierRank;

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
