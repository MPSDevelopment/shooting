package tech.shooting.tag.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.tag.enums.EventTypeEnum;

@Data
@Accessors(chain = true)
public class TagDetectedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_DETECTED;

	@JsonProperty
	private String code;
	
	@JsonProperty
	private long time;

	public TagDetectedEvent(String code) {
		this.code = code;
	}
}
