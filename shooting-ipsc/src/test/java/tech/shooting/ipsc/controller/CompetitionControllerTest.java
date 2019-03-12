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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
	private Competition save;

	private String adminToken;


	private String userToken;

	@BeforeEach
	public void before () {
		competitionRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		testing = new Competition().setName("Alladin").setLocation("Cave!");
		save = competitionRepository.save(new Competition().setName("Test name Competition"));


		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);


		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

	}

	@Test
	public void checkCreateCompetition () throws Exception {

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

	@Test
	public void checkGetCompetitionById () throws Exception {

		// try access to getCompetitionById with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace("{competitionId}", save.getId().toString())))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to getCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace("{competitionId}", save.getId().toString()))
			.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to getCompetitionById with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace("{competitionId}", save.getId().toString()))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(save.getName()));

	}

	@Test
	public void checkDeleteCompetitionById () throws Exception {

		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace("{competitionId}", save.getId().toString())))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace("{competitionId}", save.getId().toString()))
			.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to deleteCompetitionById with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace("{competitionId}", save.getId().toString()))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(save.getName()));

	}

	@Test
	public void checkUpdateCompetitionById () throws Exception {
		Competition test = competitionRepository.findByName(save.getName());
		test.setName("Update Name").setLocation("cave number 2");

		// try access to updateCompetition with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace("{competitionId}", test.getId().toString()))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to updateCompetition with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace("{competitionId}", test.getId().toString()))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))
			.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to updateCompetition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace("{competitionId}", test.getId().toString()))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(test.getName()));

	}

	@Test
	public void checkGetCount () throws Exception {

		// try access to getCount with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to getCount with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to getCount with authorized admin
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER,
			adminToken))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn()
			.getResponse();
		assertEquals(response.getContentAsString(), String.valueOf(competitionRepository.count()));
	}

	@Test
	public void checkGetAllCompetitions () throws Exception {

		// try access to getAllCompetitions with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to getAllCompetitions with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to getAllCompetitions with authorized admin
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS)
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		Competition[] actual = JacksonUtils.fromJson(Competition[].class, contentAsString);
		Competition[] exact = competitionRepository.findAll().toArray(new Competition[0]);

		assertEquals(actual.length, exact.length);
		for(int i = 0; i < actual.length; i++) {
			assertEquals(actual[i], exact[i]);
		}
	}

	@Test
	public void checkGetAllUsersByPage () throws Exception {

		createCompetition(40);

		// try to access getCompetitionsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access getCompetitionsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access getCompetitionsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}",
			String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		List<Competition> list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());

		// try to access getCompetitionsByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize" + "}", String.valueOf(30))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(20, list.size());

		// try to access to getCompetitionsByPage header with admin
		int sizeAllUser = competitionRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber" +
			"}", String
			.valueOf(page)).replace("{pageSize}", String.valueOf(size))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();

		assertEquals(response.getHeader("pages"), String.valueOf(countPages));
		assertEquals(response.getHeader("page"), String.valueOf(page));
		assertEquals(response.getHeader("total"), String.valueOf(sizeAllUser));

	}

	private void createCompetition (int count) {
		for(int i = 0; i < count; i++) {
			var competition = new Competition().setName("Test firstname" + i);
			competitionRepository.save(competition);
			log.info("Competition %s has been created", competition.getName());
		}
	}


}
