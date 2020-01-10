package tech.shooting.tag.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.tag.enums.EventTypeEnum;
import tech.shooting.tag.eventbus.Event;

@Data
@Accessors(chain = true)

public class TagFinishedEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_IMITATOR_FINISHED;

	private int laps;

	public TagFinishedEvent(int laps) {
		this.laps = laps;
	}
}
