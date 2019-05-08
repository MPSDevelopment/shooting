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
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Competition create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class CompetitionBean{
	@JsonProperty
	@ApiModelProperty(value = "Competition name", required = true)
	@NotNull(message = ValidationConstants.COMPETITION_NAME_MESSAGE)
	@Size(min = 5, max = 50, message = ValidationConstants.COMPETITION_NAME_MESSAGE)
	private String name;

	@JsonProperty("eventDate")
	@ApiModelProperty(value = "Competition date", required = true)
	private OffsetDateTime competitionDate;

	@JsonProperty("level")
	@ApiModelProperty(value = "Competition qualifier rank", notes = "For whom the competition is held")
	@NotNull(message= ValidationConstants.LEVEL_MESSAGE)
	private ClassificationBreaks qualifierRank;

	@JsonProperty("weaponType")
	@ApiModelProperty(value = "Competition type weapon", required = true)
	private WeaponTypeEnum typeWeapon;

	@JsonProperty
	@ApiModelProperty(value = "Competition rank", notes = "Level Competition")
	@NotNull
	private Rank rank;

	@JsonProperty
	@ApiModelProperty(value = "Competition location", notes = "Competition location")
	@NotNull(message = ValidationConstants.COMPETITION_LOCATION_MESSAGE)
	@Size(min = 5, max = 50, message = ValidationConstants.COMPETITION_LOCATION_MESSAGE)
	private String location;

	@JsonProperty
	@ApiModelProperty(value = "Competition active ")
	private boolean active = true;

	@JsonProperty("director")
	@ApiModelProperty(value = "Competition's Match Director", notes = "Competition's Match Director ")
	private Long matchDirector;

	@JsonProperty("mainJudge")
	@ApiModelProperty(value = "Competition's Range Master", notes = "Competition's Range Master")
	private Long rangeMaster;

	@JsonProperty("statisticsJudge")
	@ApiModelProperty(value = "Competition's Stats Officer ", notes = "Competition's Stats Officer ")
	private Long statsOfficer;

	@JsonProperty
	@ApiModelProperty(value = "List stages", notes = "Competition's list stages")
	private List<Stage> stages;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's list", notes = "Competition list competitors")
	private List<Competitor> competitors;
}
