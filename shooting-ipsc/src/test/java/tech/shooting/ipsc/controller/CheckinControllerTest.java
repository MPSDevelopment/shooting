package tech.shooting.ipsc.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.*;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.*;
import tech.shooting.ipsc.service.CheckinService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CheckinRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CheckinController.class, CheckinService.class })
class CheckinControllerTest {
	@Autowired
	private CheckinRepository checkinRepository;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private CombatNoteRepository combatNoteRepository;

	@Autowired
	private CheckinService service;

	@Autowired
	private MockMvc mockMvc;

	private Division root;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private Person testPerson;

	@BeforeEach
	void setUp() {
		checkinRepository.deleteAll();
		divisionRepository.deleteAll();
		combatNoteRepository.deleteAll();
		personRepository.deleteAll(personRepository.findByDivision(root));
		root = divisionRepository.save(new Division().setParent(null).setName("Root").setActive(true));
		testPerson = testPerson == null ? personRepository.save(new Person().setName("testing").setQualifierRank(ClassificationBreaks.D).setDivision(root)) : testPerson;
		user = user == null
				? userRepository.save(
						new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150")).setPerson(testPerson))
				: user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void getCheckList() throws Exception {
		int init = personRepository.findByDivision(root).size();
		// create 50 persons
		createPersons(50, root, false);
		// check size
		int count = personRepository.findByDivision(root).size();
		assertEquals(init + 25, count);
		// try access
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin user
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		CheckIn[] checkIns = JacksonUtils.fromJson(CheckIn[].class, contentAsString);
		assertEquals(count, checkIns.length);
		// user
		contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString())).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		checkIns = JacksonUtils.fromJson(CheckIn[].class, contentAsString);
		assertEquals(count, checkIns.length);
	}

	public void createPersons(int count, Division division, boolean flag) {
		for (int i = 0; i < count; i++) {
			Person person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			if (flag) {
				person.setDivision(division);
			} else if (i % 2 == 0) {
				person.setDivision(division);
			}
			personRepository.save(person);
			log.info("Person %s has been created", person);
		}
	}

	@Test
	void createCheck() throws Exception {
		int init = personRepository.findByDivision(root).size();
		createPersons(10, root, true);
		List<Person> byDivision = personRepository.findByDivision(root);
		int count = byDivision.size();
		assertEquals(init + 10, count);
		List<CheckinBean> fromFront = new ArrayList<>();
		for (Person p : byDivision) {
			CheckinBean bean = new CheckinBean();
			bean.setPerson(p.getId());
			bean.setStatus(TypeOfPresence.DELAY);
			fromFront.add(bean);
		}
		assertEquals(init + 10, fromFront.size());
		String json = JacksonUtils.getJson(fromFront);
		// try access
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK).header(Token.TOKEN_HEADER, judgeToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// user user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK).header(Token.TOKEN_HEADER, userToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		List<CheckinBeanToFront> listFromJson = JacksonUtils.getListFromJson(CheckinBeanToFront[].class, contentAsString);
		assertEquals(listFromJson.size(), fromFront.size());
		for (int i = 0; i < fromFront.size(); i++) {
			assertEquals(fromFront.get(i).getPerson(), listFromJson.get(i).getPerson());
			assertEquals(fromFront.get(i).getStatus(), listFromJson.get(i).getStatus());
		}
		assertEquals(checkinRepository.count(), fromFront.size());
		checkinRepository.deleteAll();
		assertEquals(checkinRepository.count(), 0);
		// admin user
		contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		listFromJson = JacksonUtils.getListFromJson(CheckinBeanToFront[].class, contentAsString);
		assertEquals(listFromJson.size(), fromFront.size());
		for (int i = 0; i < fromFront.size(); i++) {
			assertEquals(fromFront.get(i).getPerson(), listFromJson.get(i).getPerson());
			assertEquals(fromFront.get(i).getStatus(), listFromJson.get(i).getStatus());
		}
		assertEquals(checkinRepository.count(), fromFront.size());
	}

	@Test
	void checkCreateCombatNote() throws Exception {
		// try access to create combat note with admin role when check in is not exist
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_COMBAT_NOTE.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(Objects.requireNonNull(JacksonUtils.getJson(new CombatNoteBean().setCombatId(user.getPerson().getId()).setDate(OffsetDateTime.now()))))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		addDataToDB();
		List<CheckIn> checkIns = checkinRepository.findAll();
		log.info("create checks size is %s", checkIns.size());
		int sizeDelay = checkinRepository.findAllByStatus(TypeOfPresence.DELAY).size();
		int sizePresent = checkinRepository.findAllByStatus(TypeOfPresence.PRESENT).size();
		int sizeDayOff = checkinRepository.findAllByStatus(TypeOfPresence.DAY_OFF).size();
		int sizeMission = checkinRepository.findAllByStatus(TypeOfPresence.MISSION).size();
		log.info("status is : Delay %s, Present %s,Day off %s, Mission %s", sizeDelay, sizePresent, sizeDayOff, sizeMission);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_COMBAT_NOTE.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(Objects.requireNonNull(JacksonUtils.getJson(new CombatNoteBean().setCombatId(user.getId()).setDate(OffsetDateTime.now())))).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		CombatNote combatNote = JacksonUtils.fromJson(CombatNote.class, contentAsString);
		log.info("Result is \n %s", combatNote.getStatList());
	}

	@Test
	void checkGetCombatNote() throws Exception {
		addDataToDB();
		List<CheckIn> checkIns = checkinRepository.findAll();
		log.info("create checks size is %s", checkIns.size());
		int sizeDelay = checkinRepository.findAllByStatus(TypeOfPresence.DELAY).size();
		int sizePresent = checkinRepository.findAllByStatus(TypeOfPresence.PRESENT).size();
		int sizeDayOff = checkinRepository.findAllByStatus(TypeOfPresence.DAY_OFF).size();
		int sizeMission = checkinRepository.findAllByStatus(TypeOfPresence.MISSION).size();
		log.info("status is : Delay %s, Present %s,Day off %s, Mission %s", sizeDelay, sizePresent, sizeDayOff, sizeMission);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_COMBAT_NOTE.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(Objects.requireNonNull(JacksonUtils.getJson(new CombatNoteBean().setCombatId(user.getId()).setDate(OffsetDateTime.now())))).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		CombatNote combatNote = JacksonUtils.fromJson(CombatNote.class, contentAsString);
		log.info("Result is \n %s", combatNote.getStatList());
		String contentAsString1 = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_COMBAT_NOTE.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		CombatNote[] combatNotes = JacksonUtils.fromJson(CombatNote[].class, contentAsString1);
		log.info("Combat Note is %s", combatNotes[0]);
		checkNote(combatNotes[0], combatNote);
	}

	private void checkNote(CombatNote first, CombatNote second) {
		assertEquals(first.getCombat(), second.getCombat());
		assertEquals(first.getId(), second.getId());
		assertEquals(first.getDivision(), second.getDivision());
		assertEquals(first.getStatList(), second.getStatList());
		assertEquals(first.getDate(), second.getDate());
	}

	private void addDataToDB() {
		// prepare
		for (int i = 0; i < 10; i++) {
			Person person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			person.setDivision(root);
			personRepository.save(person);
		}
		List<Person> byDivision = personRepository.findByDivision(root);
		List<CheckIn> toDb = new ArrayList<>();
		for (int i = 0; i < byDivision.size(); i++) {
			if (!byDivision.get(i).equals(testPerson)) {
				if (i % 2 == 0) {
					toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(user).setStatus(TypeOfPresence.PRESENT).setDivisionId(root.getId()));
				} else {
					toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(user).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
				}
			}
		}
		toDb = checkinRepository.saveAll(toDb);
		log.info("Object in db %s ", toDb.size());
		toDb.forEach(item -> log.info("person with id\t %s\t status\t %s", item.getPerson().getId(), item.getStatus()));
		for (int i = 0; i < byDivision.size(); i++) {
			if (!byDivision.get(i).equals(testPerson)) {
				if (i % 2 == 0) {
					toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(user).setStatus(TypeOfPresence.MISSION).setDivisionId(root.getId()));
				} else {
					toDb.add(new CheckIn().setPerson(byDivision.get(i)).setOfficer(user).setStatus(TypeOfPresence.DAY_OFF).setDivisionId(root.getId()));
				}
			}
		}
		toDb = checkinRepository.saveAll(toDb);
	}

	@Test
	void check() throws BadRequestException {
		// check
		addDataToDB();
		List<Person> byDivision = personRepository.findByDivision(root);
		log.info("count person from root %s", byDivision.size());
		List<CheckIn> toDb = new ArrayList<>();
		for (Person p : byDivision) {
			toDb.add(new CheckIn().setPerson(p).setOfficer(user).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(toDb);
		log.info("count row check in from root %s", checkIns.size());
		OffsetDateTime createdDate = checkIns.get(0).getCreatedDate();
		log.info("Create date is %s", createdDate);
		List<CheckIn> allByDate = checkinRepository.findAllByDate(createdDate);
		log.info("size of list check in by date %s", allByDate.size());
		for (CheckIn check : allByDate) {
			log.info("Status is %s\n%s \t division id \t from result set search by date", check.getStatus(), check.getPerson());
		}
		log.info("Root id for search %s", root.getId());
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		log.info("Size of result search by division id %s", allByDivision.size());
		log.info("Root id for search %s and create date is %s", root.getId(), createdDate);
		List<AggBean> findByAll = checkinRepository.findAllByDivisionStatusDateInterval(root, TypeOfPresence.ALL, createdDate, TypeOfInterval.EVENING);
		List<SearchResult> fromService = service.getChecksByDivisionStatusDateInterval(new CombatListSearchBean().setDivisionId(root.getId()).setStatus(TypeOfPresence.ALL).setDate(createdDate).setInterval(TypeOfInterval.EVENING));
		assertEquals(findByAll.size(), fromService.size());
		for (int i = 0; i < fromService.size(); i++) {
			log.info("RepoMethod stat is %s \t person id  is \t %s", findByAll.get(i).getStat(), findByAll.get(i).getPerson().getId());
			log.info("Service stat is %s \t person id  is \t %s", fromService.get(i).getStatus(), fromService.get(i).getPerson().getId());
		}
	}

	@Test
	void checkGetSearchResult() throws Exception {
		// check
		addDataToDB();
		List<Person> byDivision = personRepository.findByDivision(root);
		log.info("count person from root %s", byDivision.size());
		List<CheckIn> toDb = new ArrayList<>();
		for (Person p : byDivision) {
			toDb.add(new CheckIn().setPerson(p).setOfficer(user).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(toDb);
		log.info("count row check in from root %s", checkIns.size());
		OffsetDateTime createdDate = checkIns.get(0).getCreatedDate();
		log.info("Create date is %s", createdDate);
		List<CheckIn> allByDate = checkinRepository.findAllByDate(createdDate);
		log.info("size of list check in by date %s", allByDate.size());
		for (CheckIn check : allByDate) {
			log.info("Status is %s\n%s \t division id \t from result set search by date", check.getStatus(), check.getPerson());
		}
		log.info("Root id for search %s", root.getId());
		List<CheckIn> allByDivision = checkinRepository.findAllByDivision(root.getId());
		log.info("Size of result search by division id %s", allByDivision.size());
		log.info("Root id for search %s and create date is %s", root.getId(), createdDate);

		CombatListSearchBean bean = new CombatListSearchBean().setDivisionId(root.getId()).setStatus(TypeOfPresence.ALL).setDate(createdDate).setInterval(TypeOfInterval.DAY);
		String json = JacksonUtils.getJson(bean);

		List<SearchResult> fromService = service.getChecksByDivisionStatusDateInterval(bean);
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT).header(Token.TOKEN_HEADER, judgeToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// user role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT).header(Token.TOKEN_HEADER, userToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<SearchResult> listFromJson = JacksonUtils.getListFromJson(SearchResult[].class, contentAsString);
		assertEquals(fromService, listFromJson);
		// admin role
		contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		listFromJson = JacksonUtils.getListFromJson(SearchResult[].class, contentAsString);
		assertEquals(fromService, listFromJson);
		assertEquals(11, listFromJson.size());
	}

	@Test
	void checkGetListCombatNoteByDivisionByDateById() throws Exception {
		LocalDate of = LocalDate.of(2019, 04, 20);
		LocalTime time = LocalTime.of(15, 00);
		ZoneOffset offset = OffsetDateTime.now().getOffset();
		OffsetDateTime of1 = OffsetDateTime.of(of, time, offset);

		log.info("Date is %s", of);
		CombatNote combatNote;
		for (int i = 0; i < 5; i++) {
			List<Stat> statList = new ArrayList<>();
			statList.add(new Stat().setStatus(TypeOfPresence.DAY_OFF).setCount(1));
			combatNote = new CombatNote().setCombat(user).setDate(of1).setDivision(root).setStatList(statList);
			combatNoteRepository.save(combatNote);
		}

		CombatListSearchBean bean = new CombatListSearchBean().setDivisionId(root.getId()).setDate(of1).setInterval(TypeOfInterval.DAY);
		String json = JacksonUtils.getJson(bean);

		// admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_LIST_COMBAT_NOTE_BY_DIVISION_BY_DATE_BY_INTERVAL)
				.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<CombatNote> listFromJson = JacksonUtils.getListFromJson(CombatNote[].class, contentAsString);

		log.info("Size from search is %s", listFromJson.size());
//		listFromJson.forEach(System.out::println);

//		assertEquals(5, listFromJson.size());
	}

	@Test
	void checkGetSearchResultByName() throws Exception {
		// check
		addDataToDB();
		List<Person> byDivision = personRepository.findByDivision(root);
		log.info("count person from root %s", byDivision.size());
		List<CheckIn> toDb = new ArrayList<>();
		for (Person p : byDivision) {
			toDb.add(new CheckIn().setPerson(p).setOfficer(user).setStatus(TypeOfPresence.DELAY).setDivisionId(root.getId()));
		}
		List<CheckIn> checkIns = checkinRepository.saveAll(toDb);
		log.info("count row check in from root %s", checkIns.size());
		assertEquals(11, checkIns.size());
		
		OffsetDateTime createdDate = OffsetDateTime.now();
		log.info("Create date is %s", createdDate);
		List<CheckIn> allByDate = checkinRepository.findAllByDate(createdDate);
		log.info("Rows is %s", allByDate.size());
		List<SearchResult> fromService = service.getChecksByDivisionStatusDateInterval(new CombatListSearchBean().setDivisionId(root.getId()).setStatus(TypeOfPresence.ALL).setDate(createdDate).setInterval(TypeOfInterval.DAY));
		for (int i = 0; i < fromService.size(); i++) {
			log.info("String is %s", fromService.get(i));
		}
		assertEquals(11, fromService.size());

		CombatListSearchBean bean = new CombatListSearchBean().setDivisionId(root.getId()).setDate(createdDate).setInterval(TypeOfInterval.DAY);
		String json = JacksonUtils.getJson(bean);

		// admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT_BY_NAMES).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		log.info("String result is %s", contentAsString);
		List<NameStatus> listFromJson = JacksonUtils.getListFromJson(NameStatus[].class, contentAsString);
		for (int i = 0; i < listFromJson.size(); i++) {
			log.info("Named is %s", JacksonUtils.getJson(listFromJson.get(i)));
		}
		assertEquals(11, listFromJson.size());
	}
}