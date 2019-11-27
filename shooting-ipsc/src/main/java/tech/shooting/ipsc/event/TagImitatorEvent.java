package tech.shooting.ipsc.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.eventbus.Event;
import tech.shooting.ipsc.enums.EventTypeEnum;
import tech.shooting.ipsc.pojo.Person;

@Data
@Accessors(chain = true)

public class TagImitatorEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_IMITATOR_STARTED;

	private Long standardId;

	private int laps;

	private List<Person> persons;

	private long lapDelay = 1000;

	private long personDelay = 100;

	public TagImitatorEvent(Long standardId, int laps, List<Person> list) {
		this.standardId = standardId;
		this.laps = laps;
		this.persons = list;
	}
}
