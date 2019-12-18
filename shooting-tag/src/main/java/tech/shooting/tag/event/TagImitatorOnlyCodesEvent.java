package tech.shooting.tag.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.tag.enums.EventTypeEnum;
import tech.shooting.tag.eventbus.Event;

@Data
@Accessors(chain = true)
public class TagImitatorOnlyCodesEvent extends Event {

	@JsonProperty
	private EventTypeEnum type = EventTypeEnum.TAG_IMITATOR_ONLY_CODES_STARTED;

	private int laps;

	private long lapDelay = 1000;

	private long personDelay = 100;

	private List<String> codes;

	private Long standardId;

	public TagImitatorOnlyCodesEvent(Long standardId, int laps, List<String> codes) {
		this.standardId = standardId;
		this.laps = laps;
		this.codes = codes;
	}
}
