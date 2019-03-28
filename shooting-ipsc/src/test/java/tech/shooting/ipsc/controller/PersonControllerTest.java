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
import tech.shooting.ipsc.advice.ValidationErrorHandler;
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
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.PersonService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, PersonController.class, PersonService.class, ValidationErrorHandler.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class PersonControllerTest {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

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

	private WeaponIpscCode handgun;

	private WeaponIpscCode shotgun;

	private WeaponIpscCode rifle;

	@BeforeEach
	public void before () {
		personRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		WeaponIpscCode weaponIpscCode = new WeaponIpscCode().setCode("445645645").setTypeWeapon(WeaponTypeEnum.HANDGUN);
		List<WeaponIpscCode> codes = new ArrayList<>();
		codes.add(weaponIpscCode);
		testing = personRepository.save(new Person().setName("testing").setCodes(codes).setQualifierRank(ClassificationBreaks.D));
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		handgun = new WeaponIpscCode().setTypeWeapon(WeaponTypeEnum.HANDGUN).setCode("121212121212121");
		shotgun = new WeaponIpscCode().setTypeWeapon(WeaponTypeEnum.SHOTGUN).setCode("121212121212121");
		rifle = new WeaponIpscCode().setTypeWeapon(WeaponTypeEnum.RIFLE).setCode("121212121212121");
	}

	@Test
	public void checkCreatePerson () throws Exception {
		//prepare
		PersonBean personBean = new PersonBean().setName("qwerty").setRank("noobs").setTypeWeapon(WeaponTypeEnum.HANDGUN).setQualifierRank(ClassificationBreaks.D);
		List<WeaponIpscCode> codes = new ArrayList<>();
		codes.add(handgun);
		codes.add(shotgun);
		codes.add(rifle);
		personBean.setCodes(codes);
		String json = JacksonUtils.getJson(personBean);
		//try access to createPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to createPerson() with unauthorized user without body
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).contentType(MediaType.APPLICATION_JSON_UTF8))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to createPerson() with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .content(json)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to createPerson() with user role without body
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to createPerson() with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .content(json)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to createPerson() with judge role without body
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		//try access to createPerson() with admin role without body
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                      .header(Token.TOKEN_HEADER, adminToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		//try access to createPerson() with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .content(json)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Person person = JacksonUtils.fromJson(Person.class, contentAsString);
		assertEquals(personBean.getCodes().size(), person.getCodes().size());
		assertEquals(personBean.getCodes().get(0).getCode(), person.getCodes().get(0).getCode());
		assertEquals(personBean.getCodes().get(0).getTypeWeapon(), person.getCodes().get(0).getTypeWeapon());
		assertEquals(personBean.getName(), person.getName());
		assertEquals(personBean.getRank(), person.getRank());
		assertEquals(personBean.getQualifierRank(), person.getQualifierRank());
	}

	@Test
	public void checkGetPersonById () throws Exception {
		//try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId()))))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));
	}

	@Test
	public void checkUpdatePerson () throws Exception {
		//prepare
		UpdatePerson updatePerson = new UpdatePerson();
		BeanUtils.copyProperties(testing, updatePerson);
		List<WeaponIpscCode> codes = updatePerson.getCodes();
		codes.add(shotgun);
		codes.add(rifle);
		updatePerson.setCodes(codes);
		//try to access updatePerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access updatePerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access updatePerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(JacksonUtils.getJson(testing))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access updatePerson() with admin
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(updatePerson.getId())))
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON)
		                                                               .content(JacksonUtils.getJson(updatePerson))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Person person = JacksonUtils.fromJson(Person.class, contentAsString);
		assertEquals(updatePerson.getCodes().size(), person.getCodes().size());
		//try to access updatePerson() with unauthorized user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access updatePerson() with authorized user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access updatePerson() with judge user but without context
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		//try to access updatePerson() with admin but without context
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(updatePerson.getId())))
		                                      .header(Token.TOKEN_HEADER, adminToken)
		                                      .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void checkDeletePersonById () throws Exception {
		//try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId()))))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access getPerson() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getPerson() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getPerson() with admin role when id incorrect
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, "fukdhfkdshfkjds"))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		//try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, String.valueOf(testing.getId())))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void checkGetAll () throws Exception {
		//try to access getAll() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access getAll() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getAll() with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access getAll() with admin role
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, adminToken))
		                             .andExpect(MockMvcResultMatchers.status().isOk())
		                             .andReturn();
		List<Person> listFromJson = JacksonUtils.getListFromJson(Person[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(listFromJson.size(), personRepository.findAll().size());
	}

	@Test
	public void checkGetAllPersonsByPage () throws Exception {
		createUsers(40);
		// try to access getAllPersonsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                            .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAllPersonsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                            .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access getAllPersonsByPage with admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                            .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access getAllPersonsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                 ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                                  .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
		                                                            .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<User> list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());
		// try to access getAllPersonsByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                       ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                        .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(30))).header(Token.TOKEN_HEADER, adminToken))
		                   .andExpect(MockMvcResultMatchers.status().isOk())
		                   .andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(User[].class, contentAsString);
		assertEquals(20, list.size());
	}

	@Test
	public void checkGetAllPersonsByPagePart2 () throws Exception {
		// try to access to header
		int sizeAllUser = personRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                 ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(page))
		                                                                                                                  .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(size)))
		                                                            .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(countPages));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(page));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(sizeAllUser));
	}

	private void createUsers (int count) {
		for(int i = 0; i < count; i++) {
			var user = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			personRepository.save(user);
			log.info("Person %s has been created", user.getName());
		}
	}

	@Test
	public void checkGetCount () throws Exception {
		// try to access getCount with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getCount with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access getCount with judge user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access getCount with admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
		       .andExpect(MockMvcResultMatchers.status().isOk());
		// compare getCount() & personRepository.count
		long count = personRepository.count();
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
		                             .andExpect(MockMvcResultMatchers.status().isOk())
		                             .andReturn();
		assertEquals(mvcResult.getResponse().getContentAsString(), String.valueOf(count));
	}

	@Test
	public void checkPresentEnum () throws Exception {
		//try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access with admin role
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
		TypePresent[] typePresents = JacksonUtils.fromJson(TypePresent[].class, contentAsString);
		assertEquals(TypeOfPresence.getCount(), typePresents.length);
	}
}