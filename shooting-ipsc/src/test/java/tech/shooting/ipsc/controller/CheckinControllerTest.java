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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CheckinRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.CheckinService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CheckinRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, CheckinController.class, ValidationErrorHandler.class, CheckinService.class})
class CheckinControllerTest {
	@Autowired
	private CheckinService checkinService;

	@Autowired
	private CheckinRepository checkinRepository;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private DatabaseCreator databaseCreator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

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
	void setUp () {
		checkinRepository.deleteAll();
		divisionRepository.deleteAll();
		root = divisionRepository.save(new Division().setParent(null).setName("Root").setActive(true));
		testPerson = testPerson == null ? personRepository.save(new Person().setName("testing")
		                                                                    .setCodes(List.of(new WeaponIpscCode().setCode("43423423423423").setTypeWeapon(WeaponTypeEnum.HANDGUN)))
		                                                                    .setQualifierRank(ClassificationBreaks.D)) : testPerson;
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15))
		                                                    .setName("Test firstname")
		                                                    .setPassword("dfhhjsdgfdsfhj")
		                                                    .setRoleName(RoleName.USER)
		                                                    .setAddress(new Address().setIndex("08150"))
		                                                    .setPerson(testPerson)) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void getCheckList () throws Exception {
		//check size of person list if division is new
		assertTrue(personRepository.findByDivision(root).size() == 0);
		//create 50 persons
		createPersons(50, root);
		//check  size
		int count = personRepository.findByDivision(root).size();
		assertEquals(25, count);
		//try access
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		CheckIn[] checkIns = JacksonUtils.fromJson(CheckIn[].class, contentAsString);
		assertEquals(count, checkIns.length);
		//user
		contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.CHECKIN_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, root.getId().toString()))
		                                                        .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		checkIns = JacksonUtils.fromJson(CheckIn[].class, contentAsString);
		assertEquals(count, checkIns.length);
	}

	private void createPersons (int count, Division division) {
		for(int i = 0; i < count; i++) {
			var person = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			if(i % 2 == 0) {
				person.setDivision(division);
			}
			personRepository.save(person);
			log.info("Person %s has been created", person);
		}
	}

	@Test
	void createCheck () {
		//check count check
		assertEquals(checkinRepository.count(), 0);
		//check root division
		assertEquals(divisionRepository.count(), 1);
		//check person
		assertEquals(personRepository.findById(testPerson.getId()).get(), testPerson);
		//check user where person not null
		assertNotNull(userRepository.findByPerson(testPerson));
	}
}