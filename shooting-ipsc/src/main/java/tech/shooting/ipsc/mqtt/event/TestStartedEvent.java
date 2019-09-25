package tech.shooting.ipsc.mqtt.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;
import tech.shooting.ipsc.pojo.Workspace;

@Data
public class TestStartedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TEST_STARTED;

	private Workspace worspace;

	private String comment;

	public TestStartedEvent(Workspace worspace) {
		this.worspace = worspace;
	}

	public TestStartedEvent(Workspace worspace, String comment, Object... objects) {
		this.worspace = worspace;
		this.comment = String.format(comment, objects);
	}
}
