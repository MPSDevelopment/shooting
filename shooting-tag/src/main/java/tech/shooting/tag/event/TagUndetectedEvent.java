package tech.shooting.tag.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.tag.enums.EventTypeEnum;
import tech.shooting.tag.eventbus.Event;

@Data
@Accessors(chain = true)
public class TagUndetectedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_UNDETECTED;

	@JsonProperty
	private String code;
	
	private boolean sending = true;

	public TagUndetectedEvent(String code) {
		this.code = code;
	}
}
