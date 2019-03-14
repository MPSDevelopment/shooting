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
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "competition")
@TypeAlias("competition")
@Accessors(chain = true)
public class Competition extends BaseDocument {

	public static final String NAME_FIELD = "name";
	
	public static final String STAGES_FIELD = "stages";
	
	@JsonProperty
	@ApiModelProperty(value = "Competition name", required = true)
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
	@DBRef
	private User matchDirector;

	@JsonProperty
	@ApiModelProperty(value = "Competition's Range Master", notes = "Competition's Range Master")
	@DBRef
	private User rangeMaster;

	@JsonProperty
	@ApiModelProperty(value = "Competition's Stats Officer ", notes = "Competition's Stats Officer ")
	@DBRef
	private User statsOfficer;

	@JsonProperty
	@ApiModelProperty(value = "List of stages", notes = "Competition's list stages")
	private List<Stage> stages = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "List of competitors", notes = "Competitor's list")
	private List<Competitor> competitors = new ArrayList<>();
}
