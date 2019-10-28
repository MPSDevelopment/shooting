package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;
import tech.shooting.ipsc.pojo.Workspace;

@Data
public class WorkspaceChangedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.WORKSPACE_CHANGED;

	private Workspace worspace;

	public WorkspaceChangedEvent(Workspace worspace) {
		this.worspace = worspace;
	}
}
