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
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
		TokenAuthenticationFilter.class, IpscUserDetailsService.class, PersonController.class, ValidationErrorHandler.class })
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

	private String adminToken;

	private String personJsonWithRifleCode;
	private String personJsonHandgunCode;
	private String personJsonWithShotgunCode;

	private String userToken;

	@BeforeEach
	public void before() {
		personRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);

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
	public void checkCreatePerson() throws Exception {

		// try access to createPerson() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to createPerson() with authorized non admin
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to createPerson() with authorized admin but without content
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// try access to createPerson() with authorized admin create person with rifleCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(personJsonWithRifleCode)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personWithRifleCode.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.rifleCodeIpsc").value(personWithRifleCode.getRifleCodeIpsc()));

		// try access to createPerson() with authorized admin create person with handgunCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(personJsonHandgunCode)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personWithHandgunCode.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.handgunCodeIpsc").value(personWithHandgunCode.getHandgunCodeIpsc()));

		// try access to createPerson() with authorized admin create person with shotgunCodeIpsc
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(personJsonWithShotgunCode)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(personShotgunCode.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.shotgunCodeIpsc").value(personShotgunCode.getShotgunCodeIpsc()));

	}

	@Test
	public void checkGetPersonById() throws Exception {
		Person testing = personRepository.save(new Person().setName("testing").setHandgunCodeIpsc("445645645"));

		log.info("PersonId is %s , url will be %s", testing.getId(), ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId())));

		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON.replace("{personId}", String.valueOf(testing.getId()))).header(Token.TOKEN_HEADER,
				adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testing.getId()));

	}

}