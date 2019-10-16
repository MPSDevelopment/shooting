package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;

@Data
@Accessors(chain = true)
public class TagUndetectedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_UNDETECTED;

	@JsonProperty
	private String code;

	public TagUndetectedEvent(String code) {
		this.code = code;
	}
}
