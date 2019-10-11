package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;

@Data
public class TagDetectedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_DETECTED;

	@JsonProperty
	private String code;

	public TagDetectedEvent(String code) {
		this.code = code;
	}
}
