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
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, PersonController.class, ValidationErrorHandler.class})
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
	private Person personWithRifleCode;
	private Person personWithHandgunCode;
	private Person personShotgunCode;
	private Person testing;

	private String adminToken;

	private String personJsonWithRifleCode;
	private String personJsonHandgunCode;
	private String personJsonWithShotgunCode;

	private String userToken;

	@BeforeEach
	public void before () {
		personRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		testing = personRepository.save(new Person().setName("testing").setHandgunCodeIpsc("445645645"));

		personWithRifleCode = new Person().setName("Test personRifle").setRifleCodeIpsc("123456789");
		personWithHandgunCode = new Person().setName("Test personHandgun").setHandgunCodeIpsc("789456123");
		personShotgunCode = new Person().setName("Test personShotgun").setShotgunCodeIpsc("73285945654123");

		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);

		personJsonWithRifleCode = JacksonUtils.getFullJson(personWithRifleCode);
		personJsonHandgunCode = JacksonUtils.getFullJson(personWithHandgunCode);
		personJsonWithShotgunCode = JacksonUtils.getFullJson(personShotgunCode);

		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

	}

	@Test
	public void checkCreatePerson () throws Exception {

		// try access to createPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to createPerson() with authorized non admin
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to createPerson() with authorized admin but without content
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		// try access to createPerson() with authorized admin create person with rifleCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(personJsonWithRifleCode))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personWithRifleCode.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.rifleCodeIpsc").value(personWithRifleCode.getRifleCodeIpsc()));

		// try access to createPerson() with authorized admin create person with handgunCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(personJsonHandgunCode))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personWithHandgunCode.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.handgunCodeIpsc").value(personWithHandgunCode.getHandgunCodeIpsc()));

		// try access to createPerson() with authorized admin create person with shotgunCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(personJsonWithShotgunCode))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personShotgunCode.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.shotgunCodeIpsc").value(personShotgunCode.getShotgunCodeIpsc()));

	}

	@Test
	public void checkGetPersonById () throws Exception {


		//try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId()))))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());


		//try to access getPerson() with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());


		//try to access getPerson() with admin role
		log.info("PersonId is %s , url will be %s", testing.getId(), ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId())));

		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));

	}

	@Test
	public void checkUpdatePerson () throws Exception {

		//try to access updatePerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.contentType(MediaType.APPLICATION_JSON)
			.content(JacksonUtils.getFullJson(testing))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try to access updatePerson() with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.header(Token.TOKEN_HEADER, userToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(JacksonUtils.getFullJson(testing))).andExpect(MockMvcResultMatchers.status().isForbidden());

		//try to access updatePerson() with admin
		UpdatePerson updatePerson = new UpdatePerson();
		BeanUtils.copyProperties(testing, updatePerson);
		updatePerson.setHandgunCodeIpsc("123");
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace("{personId}", String.valueOf(updatePerson.getId())))
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(JacksonUtils.getFullJson(updatePerson))).andExpect(MockMvcResultMatchers.status().isOk());

		Optional<Person> byId = personRepository.findById(updatePerson.getId());
		assertEquals(byId.get().getHandgunCodeIpsc(), updatePerson.getHandgunCodeIpsc());

		//try to access updatePerson() with admin but without context
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON.replace("{personId}", String.valueOf(updatePerson.getId())))
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}


	@Test
	public void checkDeletePersonById () throws Exception {

		//try to access getPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace("{personId}", String.valueOf(testing.getId()))))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());


		//try to access getPerson() with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());


		//try to access getPerson() with admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace("{personId}", String.valueOf(testing.getId())))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));

		//try to access getPerson() with admin role when id incorrect
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON.replace("{personId}", "1232747467497979"))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void checkGetAll () throws Exception {

		//try to access getAll() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try to access getAll() with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS).header(Token.TOKEN_HEADER, userToken))
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
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access getAllPersonsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access getAllPersonsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		List<User> list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());

		// try to access getAllPersonsByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize" + "}", String.valueOf(30))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
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
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(page))
			.replace("{pageSize}", String.valueOf(size))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(response.getHeader("pages"), String.valueOf(countPages));
		assertEquals(response.getHeader("page"), String.valueOf(page));
		assertEquals(response.getHeader("total"), String.valueOf(sizeAllUser));

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

		// try to access getCount with non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
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

}