package tech.shooting.tag.eventbus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Event {

	private String source;
	
	private String publisherId;


}
