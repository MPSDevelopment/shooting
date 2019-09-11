package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weapon;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class WeaponRepositoryTest {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private WeaponRepository weaponRepository;

	private Person person;

	private List<Weapon> list;

	private Person otherPerson;

	private Page<Weapon> page;

	private Division division;
	
	@BeforeEach
	public void before() {
		personRepository.deleteAll();
		weaponRepository.deleteAll();
		divisionRepository.deleteAll();

		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		division = divisionRepository.save(new Division().setName("First division").setParent(null));
		person = personRepository.save(new Person().setName("First person").setBirthDate(offsetDateTime).setDivision(division));
		otherPerson = personRepository.save(new Person().setName("Second person").setBirthDate(offsetDateTime).setDivision(division));
		
		weaponRepository.save(new Weapon().setCount(20).setOwner(person).setSerialNumber("AD32434234"));
		weaponRepository.save(new Weapon().setCount(20).setOwner(otherPerson).setSerialNumber("AD32432334"));
		
		log.info("Division is %s", division.getId());
	}

	@Test
	public void findByPerson() {
		list = weaponRepository.findByOwner(person);
		assertEquals(1, list.size());
	}

	@Test
	public void findByPersonIn() {
		list = weaponRepository.findByOwnerIn(Arrays.asList(person));
		assertEquals(1, list.size());
	}

	@Test
	public void findByPersonDivisionIn() {
		list = weaponRepository.findByPersonDivision(division);
		assertEquals(2, list.size());
	}

	@Test
	public void findByPersonDivisionInPageable() {
		page = weaponRepository.findByOwnerIn(personRepository.findByDivisionIn(division.getAllChildren()), PageRequest.of(0, 10));
		assertEquals(0, page.getNumber());
		assertEquals(10, page.getSize());
		assertEquals(2, page.getTotalElements());
	}
}
