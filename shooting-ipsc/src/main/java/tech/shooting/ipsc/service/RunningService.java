package tech.shooting.ipsc.service;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.event.TagDetectedEvent;
import tech.shooting.ipsc.event.TagUndetectedEvent;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.RunningData;
import tech.shooting.ipsc.repository.PersonRepository;

@Service
@Slf4j
public class RunningService {

	private Map<Person, RunningData> map = new HashedMap<>();

	@Autowired
	private PersonRepository personRepository;

	public RunningService() {
		EventBus.subscribe(this);
	}

	public RunningData getPersonData(Person person) {
		return map.get(person);
	}

	@Handler
	public void handle(TagDetectedEvent event) {
		log.info("Tag with code %s have appeared a first time");
		var person = personRepository.findByRfidCode(event.getCode()).orElse(null);
		if (person == null) {
			log.info("No person found for a rfid code %s", event.getCode());
			return;
		}
		RunningData runningData = map.get(person);
		if (runningData == null) {
			map.put(person, new RunningData().setLaps(0).setPerson(person).setLastTime(event.getTime()).setFirstTime(event.getTime()));
		} else {
			map.put(person, runningData.setLaps(runningData.getLaps() + 1).setLastTime(event.getTime()));
		}
	}

	@Handler
	public void handle(TagUndetectedEvent event) {
		log.info("Tag with code %s does not exist anymore");
	}
}
