package tech.shooting.ipsc.pojo;

import lombok.Data;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentDivisionEnum;

@Data
public class Competitor {
	
	private String id;
	private String personId;
	
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
