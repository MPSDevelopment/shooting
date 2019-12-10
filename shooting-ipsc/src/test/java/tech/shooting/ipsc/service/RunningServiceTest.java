package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
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
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.event.TagDetectedEvent;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { RunningService.class, IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class RunningServiceTest {
	
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private RunningService runningService;

	private Person person;

	@BeforeEach
	public void before() {
		personRepository.deleteAll();
	}

	@Test
	void checkTagDetecetdEvents() {
		
		person = new Person().setRfidCode("0");

		EventBus.publishEvent(new TagDetectedEvent("1").setTime(1000));
		assertNull(runningService.getPersonData(person.getRfidCode()));
		
		person = personRepository.save(new Person().setRfidCode("1"));
		
		EventBus.publishEvent(new TagDetectedEvent(person.getRfidCode()).setTime(1001));
		assertNotNull(runningService.getPersonData(person.getRfidCode()));
		assertEquals(0, runningService.getPersonData(person.getRfidCode()).getLaps());
		
		EventBus.publishEvent(new TagDetectedEvent(person.getRfidCode()).setTime(1002));
		assertNotNull(runningService.getPersonData(person.getRfidCode()));
		assertEquals(1, runningService.getPersonData(person.getRfidCode()).getLaps());
		
		EventBus.publishEvent(new TagDetectedEvent("2").setTime(1003));
		assertNotNull(runningService.getPersonData(person.getRfidCode()));
		assertEquals(1, runningService.getPersonData(person.getRfidCode()).getLaps());
		assertEquals(1001, runningService.getPersonData(person.getRfidCode()).getFirstTime());
		assertEquals(1002, runningService.getPersonData(person.getRfidCode()).getLastTime());
	}
	
	@Test
	void checkTagDetectedEventsOnlyCodes() {
		
		String personCode = "10";

		EventBus.publishEvent(new TagDetectedEvent("11").setOnlyCode(true).setTime(1000));
		assertNull(runningService.getPersonData(personCode));
		
		personCode = "11";
		
		EventBus.publishEvent(new TagDetectedEvent("11").setOnlyCode(true).setTime(1001));
		assertNotNull(runningService.getPersonData(personCode));
		assertEquals(1, runningService.getPersonData(personCode).getLaps());
		
		EventBus.publishEvent(new TagDetectedEvent("11").setOnlyCode(true).setTime(1002));
		assertNotNull(runningService.getPersonData(personCode));
		assertEquals(2, runningService.getPersonData(personCode).getLaps());
		
		EventBus.publishEvent(new TagDetectedEvent("12").setOnlyCode(true).setTime(1003));
		assertNotNull(runningService.getPersonData(personCode));
		assertEquals(2, runningService.getPersonData(personCode).getLaps());
		assertEquals(1000, runningService.getPersonData(personCode).getFirstTime());
		assertEquals(1002, runningService.getPersonData(personCode).getLastTime());
	}
	
	
}