package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;

@Data
public class CompetitionStoppedEvent extends Event {
	
	@JsonProperty
	private static final EventTypeEnum type = EventTypeEnum.COMPETITION_STOPPED;

	@JsonProperty
	private Long id;

	@JsonProperty
	private String comment;

	public CompetitionStoppedEvent(Long id) {
		this.id = id;
	}

	public CompetitionStoppedEvent(Long id, String comment, Object... objects) {
		this.id = id;
		this.comment = String.format(comment, objects);
	}
}
