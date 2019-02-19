package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentDivisionEnum;

@Document(collection = "competitor")
@TypeAlias("competitor")
@Data
@Accessors(chain = true)
public class Competitor extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Competitor's name", required = true)
	@Indexed(unique = true)
	private String name;
	
	
	
	
	
	private TournamentDivisionEnum tournamentDivision;
	
	private HandgunEnum handgunDivision;
	
//	Enum shotgunDivision
//	Enum rifleDivision
//	Enum handgunPowerFactor
//	Enum shotgunPowerFactor
//	Enum riflePowerFactor
//	Enum category
//	String tag

}
