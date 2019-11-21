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
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = DivisionRepository.class)
@ContextConfiguration(classes = { DivisionService.class, PersonService.class, IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class PersonServiceTest {
	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PersonService personService;

	@Autowired
	private DivisionService divisionService;

	private Division root;

	private Division first;

	private Division second;

	private Division third;

	@BeforeEach
	public void before() {
		divisionRepository.deleteAll();
		personRepository.deleteAll();
	}

	@Test
	void createDivision() {

		createDivisionsAndPersons();

		assertEquals(6, personService.getAllPersonsByDivision(root.getId()).size());
		assertEquals(3, personService.getAllPersonsByDivision(first.getId()).size());
		assertEquals(2, personService.getAllPersonsByDivision(second.getId()).size());
		assertEquals(1, personService.getAllPersonsByDivision(third.getId()).size());
	}

	@Test
	public void getAllPersonsByDivisionPaging() {
		
		createDivisionsAndPersons();

		var page = personService.getAllPersonsByDivisionPaging(root.getId(), 0, 10);
		assertEquals(6, page.getTotalElements());
		
		page = personService.getAllPersonsByDivisionPaging(first.getId(), 0, 10);
		assertEquals(3, page.getTotalElements());
	}
	
	@Test
	public void getPersonListByDivisionPaging() {
		
		createDivisionsAndPersons();

		var page = personService.getPersonListByDivisionPaging(root.getId(), 0, 10);
		assertEquals(6, page.getTotalElements());
		
		page = personService.getAllPersonsByDivisionPaging(first.getId(), 0, 10);
		assertEquals(3, page.getTotalElements());
	}

	private void createDivisionsAndPersons() {
		root = divisionRepository.createIfNotExists(new Division().setName("root").setParent(null));
		first = divisionService.createDivisionWithCheck(new DivisionBean().setName("first"), root.getId());
		second = divisionService.createDivisionWithCheck(new DivisionBean().setName("second"), root.getId());
		third = divisionService.createDivisionWithCheck(new DivisionBean().setName("third"), second.getId());

		personRepository.save(new Person().setName("1").setDivision(root));
		personRepository.save(new Person().setName("2").setDivision(first));
		personRepository.save(new Person().setName("3").setDivision(first));
		personRepository.save(new Person().setName("4").setDivision(first));
		personRepository.save(new Person().setName("5").setDivision(second));
		personRepository.save(new Person().setName("6").setDivision(third));
	}
}