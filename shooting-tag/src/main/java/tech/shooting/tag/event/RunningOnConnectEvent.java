package tech.shooting.tag.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.tag.enums.EventTypeEnum;

@Data
@Accessors(chain = true)
public class RunningOnConnectEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.RUNNING_CONNECT;
}
