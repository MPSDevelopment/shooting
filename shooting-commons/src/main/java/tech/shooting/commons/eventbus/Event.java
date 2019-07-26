package tech.shooting.commons.eventbus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class Event {

	private String source;
	private String publisherId;


}
