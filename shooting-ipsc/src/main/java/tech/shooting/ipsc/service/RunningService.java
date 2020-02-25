package tech.shooting.ipsc.service;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.event.RunningUpdatedEvent;
import tech.shooting.ipsc.event.TagDetectedEvent;
import tech.shooting.ipsc.event.TagFinishedEvent;
import tech.shooting.ipsc.event.TagUndetectedEvent;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.RunningData;
import tech.shooting.ipsc.repository.PersonRepository;

@Service
@Slf4j
public class RunningService {

	private static final Long DELAY = 20 * 1000L;

	/**
	 * Map of code and RunningData
	 */
	private Map<String, RunningData> map = new HashedMap<>();

	@Autowired
	private PersonRepository personRepository;

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
		Person person = null;
		if (!event.isOnlyCode()) {
			person = personRepository.findByRfidCode(event.getCode()).orElse(null);
			if (person == null) {
				log.info("No person found for a rfid code %s", event.getCode());
				return;
			}
		}
		RunningData runningData = map.get(event.getCode());

		if (runningData != null) {
			long timeDifference = (event.getTime() - runningData.getLastTime());
			if (timeDifference < DELAY) {
				log.info("Code %s appeared too early %s ms. Delay is %s ms", event.getCode(), timeDifference, DELAY);
				return;
			}
		}

		if (runningData == null) {
			runningData = new RunningData().setCode(event.getCode()).setLaps(0).setLastTime(event.getTime()).setFirstTime(event.getTime());
		} else {
			runningData = runningData.setCode(event.getCode()).setLaps(runningData.getLaps() + 1).setLastTime(event.getTime());
		}
		if (person != null) {
			runningData.setPersonId(person.getId()).setPersonName(person.getName());
		}
		map.put(event.getCode(), runningData);

		EventBus.publishEvent(new RunningUpdatedEvent().setData(runningData));
	}

	@Handler
	public void handle(TagUndetectedEvent event) {
		log.info("Tag with code %s does not exist anymore");
	}
}
