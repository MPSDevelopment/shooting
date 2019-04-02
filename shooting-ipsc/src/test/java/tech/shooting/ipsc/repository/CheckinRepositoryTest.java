package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class CheckinRepositoryTest {
	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CheckinRepository checkinRepository;

	private Division root;

	private User officer;

	private Person testPerson;

	@BeforeEach
	void setUp () {
		checkinRepository.deleteAll();
		root = divisionRepository.save(new Division().setParent(null).setName("root"));
		testPerson =
			personRepository.save(new Person().setName("testing").setCodes(List.of(new WeaponIpscCode().setCode("43423423423423").setTypeWeapon(WeaponTypeEnum.HANDGUN))).setQualifierRank(ClassificationBreaks.D));
		officer = userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15))
		                                        .setName("Test firstname")
		                                        .setPassword("dfhhjsdgfdsfhj")
		                                        .setRoleName(RoleName.USER)
		                                        .setAddress(new Address().setIndex("08150"))
		                                        .setPerson(testPerson));
	}

	@Test
	void check () {
		for(int i = 0; i < 10; i++) {
			var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			person.setDivision(root);
			personRepository.save(person);
			log.info("Person %s has been created", person);
		}
		List<Person> byDivision = personRepository.findByDivision(root);
		log.info("count person from root %s", byDivision.size());
		List<CheckIn> toDb = new ArrayList<>();
		for(Person p : byDivision) {
			toDb.add(new CheckIn().setPerson(p).setOfficer(officer).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(toDb);
		log.info("count row check in from root %s", checkIns.size());
		OffsetDateTime createdDate = checkIns.get(0).getCreatedDate();
		log.info("Create date is %s", createdDate);
		List<CheckIn> allByDate = checkinRepository.findAllByDate(createdDate);
		log.info("size of list check in by date %s", allByDate.size());
		for(CheckIn check : allByDate) {
			log.info("%s \t division id \t from result set search by date", check.getPerson());
		}
		log.info("Root id for search %s", root.getId());
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		log.info("Size of result search by division id %s", allByDivision.size());
		log.info("Root id for search %s and create date is %s", root.getId(), createdDate);
		List<CheckIn> allCurrent = checkinRepository.findAllByDateAndDivision(createdDate, root);
		log.info("Size %s \t of result search by division id %s and create date is %s", allCurrent.size(), root.getId(), createdDate);
	}
}