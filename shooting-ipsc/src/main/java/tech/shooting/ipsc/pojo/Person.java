package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;

import lombok.Data;
import tech.shooting.ipsc.enums.HandgunEnum;
import tech.shooting.ipsc.enums.TournamentDivisionEnum;

@Data
public class Person {
	
	/**
	 * Based on rfid data 
	 */
	private String id;
	
	private OffsetDateTime birthDate;
	private String familyName;
	private String givenName;
	private String initials;
	private String alias;

}
