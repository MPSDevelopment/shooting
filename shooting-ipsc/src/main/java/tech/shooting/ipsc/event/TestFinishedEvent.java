package tech.shooting.ipsc.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;
import tech.shooting.ipsc.pojo.QuizScore;
import tech.shooting.ipsc.pojo.Workspace;

@Getter
@Setter
public class TestFinishedEvent extends Event {
	
	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TEST_FINISHED;

	private Workspace worspace;

	private String comment;

	private QuizScore score;

	public TestFinishedEvent(Workspace workspace, QuizScore score) {
		this.worspace = workspace;
		this.score = score;
	}
}
