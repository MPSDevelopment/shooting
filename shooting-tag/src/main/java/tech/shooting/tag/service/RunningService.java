package tech.shooting.tag.service;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.tag.event.RunningUpdatedEvent;
import tech.shooting.tag.event.TagDetectedEvent;
import tech.shooting.tag.event.TagFinishedEvent;
import tech.shooting.tag.event.TagUndetectedEvent;
import tech.shooting.tag.eventbus.EventBus;
import tech.shooting.tag.pojo.RunningData;

@Service
@Slf4j
public class RunningService {

	private Map<String, RunningData> map = new HashedMap<>();

	public RunningService() {
		EventBus.subscribe(this);
	}

	public RunningData getPersonData(String code) {
		return map.get(code);
	}

	@Handler
	public void handle(TagFinishedEvent event) {
		map.clear();
	}

	@Handler
	public void handle(TagDetectedEvent event) {
		log.info("Tag with code %s detected", event.getCode());
		RunningData runningData = map.get(event.getCode());
		if (runningData == null) {
			runningData = new RunningData().setCode(event.getCode()).setLaps(0).setLastTime(event.getTime()).setFirstTime(event.getTime());
		} else {
			runningData = runningData.setCode(event.getCode()).setLaps(runningData.getLaps() + 1).setLastTime(event.getTime());
		}
		map.put(event.getCode(), runningData);

		EventBus.publishEvent(new RunningUpdatedEvent().setData(runningData));
	}

	@Handler
	public void handle(TagUndetectedEvent event) {
		log.info("Tag with code %s does not exist anymore", event.getCode());
	}
}
