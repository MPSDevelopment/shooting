package tech.shooting.ipsc.repository;

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
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class PersonRepositoryTest {
	
	private static final String CALL = "Bore";

	private static final String NAME = "Tigran";

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	private Division division;

	private Person person;

	private OffsetDateTime offsetDateTime;

	private Division anotherDivision;

	private Person anotherPerson;

	private Division root;

	private Division childDivision;
	
	@BeforeEach
	public void before () {
		
		personRepository.deleteAll();
		divisionRepository.deleteAll();
		
		root = divisionRepository.save(new Division().setName("Root").setParent(null));
		division = divisionRepository.save(new Division().setName("Division").setParent(root));
		childDivision = divisionRepository.save(new Division().setName("Child").setParent(division));
		anotherDivision = divisionRepository.save(new Division().setName("Another").setParent(root));
		
		offsetDateTime = OffsetDateTime.now();
		person = personRepository.save(new Person().setName(NAME).setBirthDate(offsetDateTime).setCall(CALL));
		anotherPerson = personRepository.save(new Person().setName("Another").setBirthDate(offsetDateTime).setCall("Another"));
		
		
	}

	@Test
	public void checkFindByNameAndBirthDate () {
		assertNotNull(personRepository.findByNameAndBirthDate(NAME, offsetDateTime));
		assertNull(personRepository.findByNameAndBirthDate(NAME, offsetDateTime.minusDays(1)));
	}

	@Test
	public void checkFindByDivision () {
		log.info("Set division %s", division);
		personRepository.save(person.setDivision(division));
		
		List<Person> findByDivision = personRepository.findByDivision(division);
		assertNotNull(findByDivision);
		assertEquals(1, findByDivision.size());
		
	}
	
	@Test
	public void checkFindByDivisionId () {
		List<Person> list = personRepository.findByDivisionId(division.getId());
		assertEquals(0, list.size());
		personRepository.save(person.setDivision(division));
		list = personRepository.findByDivisionId(division.getId());
		assertEquals(1, list.size());
		personRepository.save(anotherPerson.setDivision(anotherDivision));
		var anotherList = personRepository.findByDivisionId(anotherDivision.getId());
		assertEquals(1, anotherList.size());
		assertNotEquals(list, anotherList);
	}
	
	@Test
	public void checkFindByDivisionIdRecursive () {
		List<Division> list = personRepository.findByDivisionIdRecursive(root.getId());
		assertEquals(4, list.size());
		list = personRepository.findByDivisionIdRecursive(childDivision.getId());
		assertEquals(1, list.size());
	}
	
	@Test
	public void checkFindByCall() {
		assertNotNull(personRepository.findByCall(CALL).orElseGet(null));
	}
}
