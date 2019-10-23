package tech.shooting.ipsc.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.ChangeRfidCodeBean;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.RankRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.PersonService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, PersonController.class, PersonService.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class PersonControllerTest {
	
	private static final String TEST_RFID_CODE = "123";

	private static final String TEST_NUMBER = "124";

	private static final String TEST_CALL = "Bore";

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RankRepository rankRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User admin;

	private User judge;

	private Person testing;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private Rank privateRank;

	private String guestToken;

	private User guest;

	private Division root;

	@BeforeEach
	public void before() {
		personRepository.deleteAll();
		divisionRepository.deleteAll();

		root = divisionRepository.save(new Division().setName("root"));

		String password = RandomStringUtils.randomAscii(14);
		
		testing = personRepository.save(new Person().setName("testing").setQualifierRank(ClassificationBreaks.D).setRfidCode(TEST_RFID_CODE).setNumber(TEST_NUMBER).setCall(TEST_CALL));
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		guest = userRepository.findByLogin(DatabaseCreator.GUEST_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		guestToken = tokenUtils.createToken(guest.getId(), Token.TokenType.USER, guest.getLogin(), RoleName.GUEST, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

		privateRank = rankRepository.findByRus(DatabaseCreator.PRIVATE);
	}

	@Test
	public void checkCreatePerson() throws Exception {
		// prepare
		PersonBean personBean = new PersonBean().setName("qwerty").setRank(privateRank.getId()).setTypeWeapon(WeaponTypeEnum.HANDGUN).setQualifierRank(ClassificationBreaks.D);
		String json = JacksonUtils.getJson(personBean);
		// try access to createPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to createPerson() with unauthorized user without body
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to createPerson() with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, userToken).content(json)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createPerson() with user role without body
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try access to createPerson() with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, judgeToken).content(json)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createPerson() with judge role without body
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try access to createPerson() with admin role without body
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try access to createPerson() with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, adminToken).content(json)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Person person = JacksonUtils.fromJson(Person.class, contentAsString);
//		assertEquals(personBean.getCodes().size(), person.getCodes().size());
//		assertEquals(personBean.getCodes().get(0).getCode(), person.getCodes().get(0).getCode());
//		assertEquals(personBean.getCodes().get(0).getTypeWeapon(), person.getCodes().get(0).getTypeWeapon());
		assertEquals(personBean.getName(), person.getName());
		assertEquals(personBean.getRank(), person.getRank().getId());
		assertEquals(personBean.getQualifierRank(), person.getQualifierRank());
	}

	@Test
	public void checkGetPersonById() throws Exception {
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId()))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));
	}
	
	@Test
	public void checkGetPersonByRfid() throws Exception {
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_RFID_CODE.replace(ControllerAPI.REQUEST_MARK, TEST_RFID_CODE)))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_RFID_CODE.replace(ControllerAPI.REQUEST_MARK, TEST_RFID_CODE))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_RFID_CODE.replace(ControllerAPI.REQUEST_MARK, TEST_RFID_CODE))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_RFID_CODE.replace(ControllerAPI.REQUEST_MARK, TEST_RFID_CODE))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));
	}
	
	@Test
	public void checkGetPersonByNumber() throws Exception {
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_NUMBER.replace(ControllerAPI.REQUEST_MARK, TEST_NUMBER)))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_NUMBER.replace(ControllerAPI.REQUEST_MARK, TEST_NUMBER))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_NUMBER.replace(ControllerAPI.REQUEST_MARK, TEST_NUMBER))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_NUMBER.replace(ControllerAPI.REQUEST_MARK, TEST_NUMBER))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));
	}
	
	@Test
	public void checkGetFreeRfid() throws Exception {
		
		personRepository.save(new Person().setName("testing1").setQualifierRank(ClassificationBreaks.D).setRfidCode("1000"));
		personRepository.save(new Person().setName("testing2").setQualifierRank(ClassificationBreaks.D).setRfidCode("1001"));
		personRepository.save(new Person().setName("testing3").setQualifierRank(ClassificationBreaks.D).setRfidCode("1002"));
		personRepository.save(new Person().setName("testing4").setQualifierRank(ClassificationBreaks.D).setRfidCode("1003"));
		
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_RFID))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_RFID)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_RFID)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_RFID)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.number").value("1004"));
	}
	
	@Test
	public void checkGetFreeNumber() throws Exception {
		
		personRepository.save(new Person().setName("testing1").setQualifierRank(ClassificationBreaks.D).setNumber("1"));
		personRepository.save(new Person().setName("testing2").setQualifierRank(ClassificationBreaks.D).setNumber("2"));
		personRepository.save(new Person().setName("testing3").setQualifierRank(ClassificationBreaks.D).setNumber("3"));
		personRepository.save(new Person().setName("testing4").setQualifierRank(ClassificationBreaks.D).setNumber("4"));
		
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_NUMBER))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_NUMBER)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_NUMBER)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_FREE_NUMBER)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.number").value("5"));
	}
	
	@Test
	public void checkGetPersonByCall() throws Exception {
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_CALL.replace(ControllerAPI.REQUEST_PERSON_ID, TEST_CALL)))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_CALL.replace(ControllerAPI.REQUEST_PERSON_ID, TEST_CALL))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_CALL.replace(ControllerAPI.REQUEST_PERSON_ID, TEST_CALL))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON_BY_CALL.replace(ControllerAPI.REQUEST_PERSON_ID, TEST_CALL))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));
	}

	@Test
	public void checkUpdatePerson() throws Exception {
		// prepare
		UpdatePerson updatePerson = new UpdatePerson();
		BeanUtils.copyProperties(testing, updatePerson);
		
		// try to access updatePerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access updatePerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access updatePerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access updatePerson() with admin
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(updatePerson.getId())))
						.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(updatePerson)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Person person = JacksonUtils.fromJson(Person.class, contentAsString);
//		assertEquals(updatePerson.getCodes().size(), person.getCodes().size());
		// try to access updatePerson() with unauthorized user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access updatePerson() with authorized user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to access updatePerson() with judge user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to access updatePerson() with admin but without context
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(updatePerson.getId())))
				.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void checkUpdateRfidCode() throws Exception {
		// prepare
		var updatePerson = new ChangeRfidCodeBean();
		updatePerson.setId(testing.getId());
		updatePerson.setRfidCode("1234");
		
		assertNotEquals(updatePerson.getRfidCode(), testing.getRfidCode());
		// try to access updatePerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON_RFID)
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(updatePerson))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access updatePerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON_RFID)
				.header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(updatePerson))).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access updatePerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON_RFID)
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(updatePerson))).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access updatePerson() with admin
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON_RFID)
						.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(updatePerson)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Person person = JacksonUtils.fromJson(Person.class, contentAsString);
		assertEquals(updatePerson.getRfidCode(), person.getRfidCode());
	}

	@Test
	public void checkDeletePersonById() throws Exception {
		// try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId()))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getPerson() with admin role when id incorrect
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, "fukdhfkdshfkjds"))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void checkGetAll() throws Exception {
		// try to access getAll() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAll() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getAll() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getAll() with admin role
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<Person> listFromJson = JacksonUtils.getListFromJson(Person[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(listFromJson.size(), personRepository.findAll().size());

		// try to access getAll() with guest role
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, guestToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		listFromJson = JacksonUtils.getListFromJson(Person[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(listFromJson.size(), personRepository.findAll().size());
	}

	@Test
	public void checkGetAllPersonsByPage() throws Exception {
		createUsers(40);
		// try to access getAllPersonsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAllPersonsByPage with authorized user
		mockMvc.perform(
				MockMvcRequestBuilders
						.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getAllPersonsByPage with admin user
		mockMvc.perform(
				MockMvcRequestBuilders
						.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getAllPersonsByPage with admin user
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders
						.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<User> list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());
		// try to access getAllPersonsByPage with admin user with size 30
		mvcResult = mockMvc
				.perform(MockMvcRequestBuilders
						.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(30)))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(User[].class, contentAsString);
		assertEquals(20, list.size());
	}

	@Test
	public void checkGetAllPersonsByDivisionByPage() throws Exception {
		createUsers(40);
		// try to access getAllPersonsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_DIVISION_ID, String.valueOf(root.getId()))
				.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAllPersonsByPage with authorized user
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_DIVISION_ID, String.valueOf(root.getId()))
				.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(10))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(2));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(1));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(20));
	}

	@Test
	public void checkGetAllPersonsByPagePart2() throws Exception {
		// try to access to header
		int sizeAllUser = personRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(page)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(size)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(countPages));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(page));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(sizeAllUser));
	}

	private void createUsers(int count) {
		for (int i = 0; i < count; i++) {
			Person user = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			if (i % 2 == 0) {
				user.setDivision(root);
			}
			personRepository.save(user);
			log.info("Person %s has been created", user.getName());
		}
	}

	@Test
	public void checkGetCount() throws Exception {
		// try to access getCount with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getCount with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getCount with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getCount with admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// compare getCount() & personRepository.count
		long count = personRepository.count();
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		assertEquals(mvcResult.getResponse().getContentAsString(), String.valueOf(count));
	}

	@Test
	public void checkPresentEnum() throws Exception {
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		TypePresent[] typePresents = JacksonUtils.fromJson(TypePresent[].class, contentAsString);
		assertEquals(TypeOfPresence.getCount(), typePresents.length);
	}
}