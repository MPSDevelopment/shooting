package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;

@Data
@Accessors(chain = true)
public class TagDetectedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_DETECTED;

	@JsonProperty
	private String code;
	
	@JsonProperty
	private boolean onlyCode = false;	
	
	@JsonProperty
	private long time;

	public TagDetectedEvent(String code) {
		this.code = code;
	}
}
