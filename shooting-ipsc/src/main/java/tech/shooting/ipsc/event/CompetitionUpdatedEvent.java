package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;

@Data
public class CompetitionUpdatedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.COMPETITION_UPDATED;

	@JsonProperty
	private Long id;

	@JsonProperty
	private String comment;

	public CompetitionUpdatedEvent(Long id) {
		this.id = id;
	}

	public CompetitionUpdatedEvent(Long id, String comment, Object... objects) {
		this.id = id;
		this.comment = String.format(comment, objects);
	}
}
