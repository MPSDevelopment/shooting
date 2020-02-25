package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

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
import tech.shooting.ipsc.event.TagFinishedEvent;
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

	private Map<Long, TestRunningData> map = new HashMap<>();

	@BeforeEach
	public void beforeEach() {
		personRepository.deleteAll();
	}

	@Test
	public void handleTagImitatorEvent() throws InterruptedException {

		EventBus.subscribe(this);

		var standard = standardRepository.save(new Standard().setLaps(4).setRunning(true));
		var thor = personRepository.save(new Person().setName("Thor").setRfidCode("1234"));
		var loki = personRepository.save(new Person().setName("Loki").setRfidCode("1235"));

		Thread.sleep(10);

		EventBus.publishEvent(new TagImitatorEvent(standard.getId(), standard.getLaps(), personRepository.findAll()).setLapDelay(200).setPersonDelay(100));

		assertEquals(5, map.get(thor.getId()).getCount());
		assertEquals(5, map.get(loki.getId()).getCount());
		assertEquals(4, map.get(thor.getId()).getPreviousLaps());
		assertEquals(4, map.get(loki.getId()).getPreviousLaps());

		Thread.sleep(10);

		EventBus.publishEvent(new TagImitatorEvent(standard.getId(), standard.getLaps(), personRepository.findAll()).setLapDelay(200).setPersonDelay(100));

		assertEquals(5, map.get(thor.getId()).getCount());
		assertEquals(5, map.get(loki.getId()).getCount());
		assertEquals(4, map.get(thor.getId()).getPreviousLaps());
		assertEquals(4, map.get(loki.getId()).getPreviousLaps());
	}

	@Handler
	public void handle(TagFinishedEvent event) {
		map.clear();
	}

	@Handler
	public void handle(RunningUpdatedEvent event) {

		var testRunningData = map.get(event.getData().getPersonId());

		if (testRunningData != null) {
			log.info("Running update event: Person %s Laps %s(%s) Time %s(%s)", event.getData().getPersonId(), event.getData().getLaps(), testRunningData.getPreviousLaps(), event.getData().getLastTime(), testRunningData.getPreviousTime());
		} else {
			testRunningData = new TestRunningData();
			log.info("Running update event: Person %s Laps %s(0) Time %s(0)", event.getData().getPersonId(), event.getData().getLaps(), event.getData().getLastTime());
		}

		if (testRunningData != null && testRunningData.getPreviousFirstTime() != 0) {
			assertEquals(event.getData().getFirstTime(), testRunningData.getPreviousFirstTime());
			assertNotEquals(event.getData().getLastTime(), testRunningData.getPreviousTime());
		}
		assertNotEquals(event.getData().getLaps(), testRunningData.getPreviousLaps());

		testRunningData.setPreviousLaps(event.getData().getLaps());
		testRunningData.setPreviousTime(event.getData().getLastTime());
		testRunningData.setPreviousFirstTime(event.getData().getFirstTime());
		testRunningData.setCount(testRunningData.getCount() + 1);

		map.put(event.getData().getPersonId(), testRunningData);
	}

}
