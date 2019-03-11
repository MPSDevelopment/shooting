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
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, CompetitionController.class, ValidationErrorHandler.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class CompetitionControllerTest {
	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;
	private User admin;

	private Competition testing;

	private String adminToken;


	private String userToken;

	@BeforeEach
	public void before () {
		competitionRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		testing = new Competition().setName("Alladin");


		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);


		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

	}

	@Test
	public void checkCreatePerson () throws Exception {

		// try access to createCompetition() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to createCompetition() with authorized non admin
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to createCompetition() with authorized admin but without content
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		// try access to createCompetition() with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(JacksonUtils.getFullJson(testing))).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testing.getName()));


	}
}