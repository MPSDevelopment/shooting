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
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.AggBean;
import tech.shooting.ipsc.bean.SearchResult;
import tech.shooting.ipsc.bean.Stat;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.service.CheckinService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
class CheckinRepositoryTest {
	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CheckinRepository checkinRepository;

	@Autowired
	private CheckinService service;

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
	void check () throws BadRequestException {
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
			log.info("Status is %s\n%s \t division id \t from result set search by date", check.getStatus(), check.getPerson());
		}
		log.info("Root id for search %s", root.getId());
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		log.info("Size of result search by division id %s", allByDivision.size());
		log.info("Root id for search %s and create date is %s", root.getId(), createdDate);
		var findByAll = checkinRepository.findAllByDivisionStatusDateInterval(root, TypeOfPresence.ALL, createdDate, TypeOfInterval.EVENING);
		List<SearchResult> fromService = service.getChecksByDivisionStatusDateInterval(root.getId(), TypeOfPresence.ALL, createdDate, TypeOfInterval.EVENING);
		assertEquals(findByAll.size(), fromService.size());
	}

	@Test
	void checkDate () {
		addDataToDB();
		List<CheckIn> checkIns = checkinRepository.findAll();
		log.info("status row check in from root %s", checkIns);
		for(int i = 0; i < checkIns.size(); i++) {
			log.info("check is id %s\t status is %s\t\t date is %s", checkIns.get(i).getId(), checkIns.get(i).getStatus(), checkIns.get(i).getCreatedDate());
		}
		log.info("All rows %s", checkinRepository.findAll().size());
		log.info("Status delay is %s", checkinRepository.findAllByStatus(TypeOfPresence.DELAY).size());
		log.info("Status present is %s", checkinRepository.findAllByStatus(TypeOfPresence.PRESENT).size());
		log.info("Status day off is %s", checkinRepository.findAllByStatus(TypeOfPresence.DAY_OFF).size());
		log.info("Status mission is %s", checkinRepository.findAllByStatus(TypeOfPresence.MISSION).size());
	}

	@Test
	void checkAggregation () {
		//prepare
		for(int i = 0; i < 10; i++) {
			var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			person.setDivision(root);
			personRepository.save(person);
		}
		List<Person> byDivision = personRepository.findByDivision(root);
		List<CheckIn> toDb = new ArrayList<>();
		for(int i = 0; i < byDivision.size(); i++) {
			if(i % 2 == 0) {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.PRESENT).setDivisionId(root.getId()));
			} else {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
			}
		}
		toDb = checkinRepository.saveAll(toDb);
		log.info("Object in db %s ", toDb.size());
		toDb.forEach(item -> log.info("person with id\t %s\t status\t %s", item.getPerson().getId(), item.getStatus()));
		for(int i = 0; i < byDivision.size(); i++) {
			if(i % 2 == 0) {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.MISSION).setDivisionId(root.getId()));
			} else {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.DAY_OFF).setDivisionId(root.getId()));
			}
		}
		toDb = checkinRepository.saveAll(toDb);
		log.info("Object in db %s ", toDb.size());
		toDb.forEach(item -> log.info("person with id\t %s\t status\t %s", item.getPerson().getId(), item.getStatus()));
	}

	private void addDataToDB () {
		//prepare
		for(int i = 0; i < 10; i++) {
			var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			person.setDivision(root);
			personRepository.save(person);
		}
		List<Person> byDivision = personRepository.findByDivision(root);
		List<CheckIn> toDb = new ArrayList<>();
		for(int i = 0; i < byDivision.size(); i++) {
			if(i % 2 == 0) {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.PRESENT).setDivisionId(root.getId()));
			} else {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
			}
		}
		toDb = checkinRepository.saveAll(toDb);
		log.info("Object in db %s ", toDb.size());
		toDb.forEach(item -> log.info("person with id\t %s\t status\t %s", item.getPerson().getId(), item.getStatus()));
		for(int i = 0; i < byDivision.size(); i++) {
			if(i % 2 == 0) {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.MISSION).setDivisionId(root.getId()));
			} else {
				toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(officer).setStatus(TypeOfPresence.DAY_OFF).setDivisionId(root.getId()));
			}
		}
		toDb = checkinRepository.saveAll(toDb);
	}

	@Test
	void checkFindAllByDivisionStatusDateInterval () {
		addDataToDB();
		List<CheckIn> all = checkinRepository.findAll();
		log.info("Size is %s", all.size());
		List<CheckIn> allByStatus = checkinRepository.findAllByStatus(TypeOfPresence.PRESENT);
		log.info("Size status PRESENT is %s ", allByStatus.size());
		assertTrue(all.size() != 0);
		List<AggBean> allByDivisionStatusDateInterval = checkinRepository.findAllByDivisionStatusDateInterval(root, TypeOfPresence.ALL, OffsetDateTime.now(), TypeOfInterval.EVENING);
		log.info("Size is %s", allByDivisionStatusDateInterval.size());
		for(AggBean aggBean : allByDivisionStatusDateInterval) {
			log.info("Person %s \n status %s", aggBean.getPerson(), aggBean.getStat());
		}
	}

	@Test
	void checkFindAllByCreatedDateAndOfficer () {
		//prepare
		var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
		person.setDivision(root);
		Person save = personRepository.save(person);
		CheckIn saveCheckIn = checkinRepository.save(new CheckIn().setDivisionId(root.getId()).setStatus(TypeOfPresence.PRESENT).setPerson(save).setOfficer(officer));
		//check
		List<CheckIn> allByCreatedDateAndOfficer = checkinRepository.findAllByCreatedDateAndOfficer(saveCheckIn.getCreatedDate(), officer);
		assertEquals(allByCreatedDateAndOfficer.size(), 1);
		assertEquals(saveCheckIn, allByCreatedDateAndOfficer.get(0));
	}

	@Test
	void checkFindAllByDateAndDivision () {
		//prepare
		addDataToDB();
		OffsetDateTime now = OffsetDateTime.now();
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		for(int i = 0; i < allByDivision.size(); i++) {
			allByDivision.get(i).setCreatedDate(now);
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(allByDivision);
		for(int i = 0; i < checkIns.size(); i++) {
			assertTrue(checkIns.get(i).getCreatedDate().equals(now));
		}
		//check
		List<CheckIn> allByDateAndDivision = checkinRepository.findAllByDateAndDivision(now, root);
		assertEquals(checkIns, allByDateAndDivision);
	}

	@Test
	void checkFindAllByDateAndRootDivision () {
		//prepare
		addDataToDB();
		OffsetDateTime createDate = OffsetDateTime.now();
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		for(int i = 0; i < allByDivision.size(); i++) {
			allByDivision.get(i).setCreatedDate(createDate);
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(allByDivision);
		for(int i = 0; i < checkIns.size(); i++) {
			assertTrue(checkIns.get(i).getCreatedDate().equals(createDate));
		}
		log.info("size check in report for root division %s", checkIns.size());
		//create subdivision
		Division subroot = divisionRepository.save(new Division().setParent(root).setName("subroot").setActive(true));
		List<Division> children = root.getChildren();
		children.add(subroot);
		root.setChildren(children);
		//added subDivision to root list children
		divisionRepository.save(root);
		//create person in subdivision
		var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
		person.setDivision(subroot);
		Person save = personRepository.save(person);
		//save checkin for subdivision
		CheckIn save1 = checkinRepository.save(new CheckIn().setPerson(save).setOfficer(officer).setStatus(TypeOfPresence.MISSION).setDivisionId(subroot.getId()));
		assertNotEquals(save1.getCreatedDate(), createDate);
		save1.setCreatedDate(createDate);
		save1 = checkinRepository.save(save1);
		assertEquals(save1.getCreatedDate(), createDate);
		List<CheckIn> findByRoot = checkinRepository.findAllByDateAndRootDivision(createDate, root);
		assertEquals(checkIns.size() + 1, findByRoot.size());
		log.info("Size count fot sub root %s", findByRoot.size());
	}

	@Test
	void checkFindAllByDivision () {
		//prepare
		addDataToDB();
		assertEquals(checkinRepository.findAll().size(), 20);
		Division subroot = divisionRepository.save(new Division().setParent(root).setName("subroot").setActive(true));
		List<Division> children = root.getChildren();
		children.add(subroot);
		root.setChildren(children);
		//added subDivision to root list children
		divisionRepository.save(root);
		//create person in subdivision
		var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
		person.setDivision(subroot);
		Person save = personRepository.save(person);
		//save checkin for subdivision
		CheckIn save1 = checkinRepository.save(new CheckIn().setPerson(save).setOfficer(officer).setStatus(TypeOfPresence.MISSION).setDivisionId(subroot.getId()));
		//check
		assertEquals(checkinRepository.findAllByDivision(root.getId()).size(), 20);
		assertEquals(checkinRepository.findAll().size(), 21);
	}

	@Test
	void checkFindAllByDate () {
		//prepare
		addDataToDB();
		List<CheckIn> all = checkinRepository.findAll();
		assertEquals(all.size(), 20);
		OffsetDateTime second = all.get(1).getCreatedDate();
		assertNotNull(second);
		int count = 0;
		for(int i = 0; i < all.size(); i++) {
			if(all.get(i).getCreatedDate().equals(second)) {
				count++;
			}
		}
		//check
		List<CheckIn> allByDate = checkinRepository.findAllByDate(second);
		assertEquals(allByDate.size(), count);
		assertNotNull(allByDate.get(0));
	}

	@Test
	void checkFindAllByStatus () {
		//prepare
		addDataToDB();
		List<CheckIn> all = checkinRepository.findAll();
		int count = 0;
		for(int i = 0; i < all.size(); i++) {
			if(all.get(i).getStatus().equals(TypeOfPresence.PRESENT)) {
				count++;
			}
		}
		log.info("Count of Present status is %s", count);
		//check
		assertEquals(count, checkinRepository.findAllByStatus(TypeOfPresence.PRESENT).size());
	}

	@Test
	void checkGetCombatNoteByDivisionFromPeriod () {
		//prepare
		addDataToDB();
		List<CheckIn> all = checkinRepository.findAll();
		int countPresent = 0;
		int countDelay = 0;
		int countDayOff = 0;
		int countMission = 0;
		for(int i = 0; i < all.size(); i++) {
			TypeOfPresence status = all.get(i).getStatus();
			if(status.equals(TypeOfPresence.PRESENT)) {
				countPresent++;
			} else if(status.equals(TypeOfPresence.DAY_OFF)) {
				countDayOff++;
			} else if(status.equals(TypeOfPresence.MISSION)) {
				countMission++;
			} else if(status.equals(TypeOfPresence.DELAY)) {
				countDelay++;
			} else {
				log.info("Status is %s", status);
			}
		}
		log.info("Status delay is %s", countDelay);
		log.info("Status present is %s", countPresent);
		log.info("Status day off is %s", countDayOff);
		log.info("Status mission is %s", countMission);
		List<Stat> custom = new ArrayList<>();
		custom.add(new Stat().setStatus(TypeOfPresence.PRESENT).setCount(countPresent));
		custom.add(new Stat().setStatus(TypeOfPresence.DELAY).setCount(countDelay));
		custom.add(new Stat().setStatus(TypeOfPresence.DAY_OFF).setCount(countDayOff));
		custom.add(new Stat().setStatus(TypeOfPresence.MISSION).setCount(countMission));
		List<Stat> combatNoteByDivisionFromPeriod = checkinRepository.getCombatNoteByDivisionFromPeriod(root, all.get(0).getCreatedDate(), TypeOfInterval.EVENING);
		assertEquals(custom.size(), combatNoteByDivisionFromPeriod.size());
		for(int i = 0; i < combatNoteByDivisionFromPeriod.size(); i++) {
			assertTrue(custom.contains(combatNoteByDivisionFromPeriod.get(i)));
		}
	}
}