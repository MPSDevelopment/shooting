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

	private static final Long DELAY = 20 * 1000L;

	/**
	 * Map of code and RunningData
	 */
	private Map<String, RunningData> map = new HashedMap<>();

	private int laps;

	public RunningService() {
		EventBus.subscribe(this);
	}

	public RunningData getPersonData(String code) {
		return map.get(code);
	}

	@Handler
	public void handle(TagFinishedEvent event) {
		map.clear();
		this.laps = event.getLaps();
		log.info("Running a standard with %s laps", this.laps);
	}

	@Handler
	public void handle(TagDetectedEvent event) {
		RunningData runningData = map.get(event.getCode());

		int laps = 0;
		
		if (runningData != null) {
			long timeDifference = (event.getTime() - runningData.getLastTime());
			if (timeDifference < DELAY) {
				log.info("Code %s appeared too early %s ms. Delay is %s ms", event.getCode(), timeDifference, DELAY);
				return;
			}
		}

		if (runningData == null) {
			runningData = new RunningData().setCode(event.getCode()).setLastTime(event.getTime()).setFirstTime(event.getTime());
		} else {
			laps = runningData.getLaps() + 1;
			runningData = runningData.setCode(event.getCode()).setLastTime(event.getTime());
		}
		
		log.info("Tag with code %s detected. It is %s laps for this code", event.getCode(), laps);

		if (laps > this.laps) {
			log.debug("Lap %s bigger then standard laps %s. Will not send the message", laps, this.laps);
		} else {
			runningData.setLaps(laps);
			EventBus.publishEvent(new RunningUpdatedEvent().setSending(event.isSending()).setData(runningData));
		}
		
		map.put(event.getCode(), runningData);
	}

	@Handler
	public void handle(TagUndetectedEvent event) {
		log.info("Tag with code %s does not exist anymore", event.getCode());
	}
}
