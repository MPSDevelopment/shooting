package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;
import tech.shooting.ipsc.pojo.RunningData;

@Data
@Accessors(chain = true)
public class RunningCompletedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.RUNNING_COMPLETED;
	
	@JsonProperty
	private RunningData data;
}
