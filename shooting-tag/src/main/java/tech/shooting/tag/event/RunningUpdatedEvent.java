package tech.shooting.tag.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.tag.enums.EventTypeEnum;
import tech.shooting.tag.pojo.RunningData;

@Data
@Accessors(chain = true)
public class RunningUpdatedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.RUNNING_UPDATED;

	@JsonProperty
	private Long personId;
	
	@JsonProperty
	private RunningData data;
}
