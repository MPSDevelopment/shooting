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

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class PersonRepositoryTest {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;
	@BeforeEach
	public void before () {
		personRepository.deleteAll();
	}

	@Test
	public void checkFindByNameAndBirthDate () {
		String name = "Tigran";
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		personRepository.save(new Person().setName(name).setBirthDate(offsetDateTime));
		assertNotNull(personRepository.findByNameAndBirthDate(name, offsetDateTime));
	}

	@Test
	public void checkFindByDivision () {
		String name = "Tigran";
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		List<Division> all = divisionRepository.findAll();
		Division division;
		if(all.size() == 0) {
			division = divisionRepository.save(new Division().setName("fdfdfdfd").setParent(null));
		} else {
			division = all.get(0);
		}
		log.info("Set division %s", division);
		personRepository.save(new Person().setName(name).setBirthDate(offsetDateTime).setDivision(division));
		assertNotNull(personRepository.findByDivision(division));
	}
}
