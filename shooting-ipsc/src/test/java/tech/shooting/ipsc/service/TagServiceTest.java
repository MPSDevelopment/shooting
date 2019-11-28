package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.event.RunningUpdatedEvent;
import tech.shooting.ipsc.event.TagImitatorEvent;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.StandardRepository;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { TagService.class, RunningService.class, SettingsService.class, IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class TagServiceTest {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private TagService tagService;

	private int count;

	private int previousLaps = -1;

	private long previousTime;

	private long previousFirstTime;

	@BeforeEach
	public void beforeEach() {
		personRepository.deleteAll();
	}

	@Test
	public void handleTagImitatorEvent() {

		EventBus.subscribe(this);

		var standard = standardRepository.save(new Standard().setLaps(4).setRunning(true));
		personRepository.save(new Person().setName("Thor").setRfidCode("1234"));

		EventBus.publishEvent(new TagImitatorEvent(standard.getId(), standard.getLaps(), personRepository.findAll()).setLapDelay(200).setPersonDelay(100));

		assertEquals(5, count);
	}

	@Handler
	public void handle(RunningUpdatedEvent event) {

		log.info("Running update event: Person %s Laps %s(%s) Time %s(%s)", event.getData().getPersonId(), event.getData().getLaps(), previousLaps, event.getData().getLastTime(), previousTime);

		if (previousFirstTime != 0) {
			assertEquals(event.getData().getFirstTime(), previousFirstTime);
			assertNotEquals(event.getData().getLastTime(), previousTime);
		}
		assertNotEquals(event.getData().getLaps(), previousLaps);

		previousLaps = event.getData().getLaps();
		previousTime = event.getData().getLastTime();
		previousFirstTime = event.getData().getFirstTime();
		
		count++;
	}

}
