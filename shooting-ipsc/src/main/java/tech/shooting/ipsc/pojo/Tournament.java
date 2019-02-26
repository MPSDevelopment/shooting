package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentCategoryEnum;
import tech.shooting.ipsc.enums.TournamentLevelEnum;
import tech.shooting.ipsc.enums.TournamentTypeEnum;

/**
 * 
 * СОРЕВНОВАНИЕ. Состоит минимум из 3 упражнений, на каждом из которых
используется один и тот же вид оружия. Победитель соревнования определяется по сумме
результатов всех упражнений.
 * 
 * Специальное соревнование, в котором отдельные упражнения предназначены
для одного отдельного вида оружия (например, Упражнения 1-4 «пистолет», Упражнения
5-8 «карабин», Упражнения 9-12 «ружьё»). Победитель турнира определяется по сумме
результатов всех упражнений.
 * 
 * @author Viking
 *
 */

@Document(collection = "tournament")
@TypeAlias("tournament")
@Data
@Accessors(chain = true)
public class Tournament extends BaseDocument {

	public static final String NAME_FIELD = "name";

	@JsonProperty
	@ApiModelProperty(value = "Tournament's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's type", required = true)
	private TournamentTypeEnum type;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's level", required = true)
	private TournamentLevelEnum level;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's category", required = true)
	private TournamentCategoryEnum category;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's begin date")
	private OffsetDateTime beginDate;
	
	@JsonProperty
	@ApiModelProperty(value = "Tournament's end date")
	private OffsetDateTime endDate;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's director", required = true)
	private User director;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's main official", required = true)
	private User mainOfficial;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's statistics official", required = true)
	private User statisticsOfficial;

	@JsonProperty
	@ApiModelProperty(value = "Tournament's active ")
	private boolean active = true;
	
	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Tournament's exercise list ")
	private List<Exercise> exercises;
}
