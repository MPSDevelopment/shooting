package tech.shooting.ipsc.mqtt.event;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;

@Data
public class CompetitionUpdatedEvent extends Event {

	private Long id;

	public CompetitionUpdatedEvent(Long id) {
		this.id = id;
	}
}

