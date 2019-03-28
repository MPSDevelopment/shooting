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
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.CompetitorMark;
import tech.shooting.ipsc.bean.ScoreBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.enums.TypeMarkEnum;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.CompetitionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, CompetitionController.class, ValidationErrorHandler.class, CompetitionService.class})
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
	private PersonRepository personRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User admin;

	private User judge;

	private Competition competition;

	private Competition testingCompetition;

	private Person testingPerson;

	private Competitor testingCompetitor;

	private Stage testingStage;

	private String stageJson;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private List<Stage> stages;

	@BeforeEach
	public void before () {
		competitionRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		competition = new Competition().setName("Alladin").setLocation("Cave!");
		testingCompetition = competitionRepository.save(new Competition().setName("Test name Competition"));
		testingPerson = personRepository.save(new Person().setName("testing testingPerson for competitor"));
		testingCompetitor = new Competitor().setName("testing testingPerson for competitor").setRfidCode("1234567890").setPerson(testingPerson);
		testingStage = new Stage().setName("Testing testingStage").setTargets(20).setNumberOfRoundToBeScored(5).setMaximumPoints(25);
		stageJson = JacksonUtils.getJson(testingStage);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN) == null ? userRepository.save(new User().setLogin("judge")
		                                                                                                        .setRoleName(RoleName.JUDGE)
		                                                                                                        .setName("judge_name")
		                                                                                                        .setPassword(RandomStringUtils.randomAscii(14))) : userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	//utils method's
	private Stage findStage (Competition competition, Stage testingStage) {
		return competition.getStages()
		                  .stream()
		                  .filter((stage) -> stage.getName().equals(testingStage.getName()) && stage.getNumberOfRoundToBeScored().equals(testingStage.getNumberOfRoundToBeScored()) &&
		                                     stage.getTargets().equals(testingStage.getTargets()) && stage.getMaximumPoints().equals(testingStage.getMaximumPoints()))
		                  .findAny()
		                  .get();
	}

	private void testRequiredFields (Stage stageFromResponse, Stage testingStage) {
		assertEquals(stageFromResponse.getTargets(), testingStage.getTargets());
		assertEquals(stageFromResponse.getName(), testingStage.getName());
		assertEquals(stageFromResponse.getMaximumPoints(), testingStage.getMaximumPoints());
		assertEquals(stageFromResponse.getNumberOfRoundToBeScored(), testingStage.getNumberOfRoundToBeScored());
	}

	private CompetitionBean setupCompetitionBean (Competition competition) {
		CompetitionBean competitionBean = new CompetitionBean();
		BeanUtils.copyProperties(competition, competitionBean, Competition.MATCH_DIRECTOR_FIELD, Competition.RANGE_MASTER_FIELD, Competition.STATS_OFFICER_FIELD);
		if(competition.getRangeMaster() != null) {
			competitionBean.setRangeMaster(competition.getRangeMaster().getId());
		}
		if(competition.getMatchDirector() != null) {
			competitionBean.setMatchDirector(competition.getMatchDirector().getId());
		}
		if(competition.getStatsOfficer() != null) {
			competitionBean.setStatsOfficer(competition.getStatsOfficer().getId());
		}
		return competitionBean;
	}

	@Test
	public void checkCreateCompetition () throws Exception {
		CompetitionBean competitionBean = setupCompetitionBean(competition);
		// try access to createCompetition() with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getFullJson(competitionBean))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to createCompetition() with authorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getFullJson(competitionBean))).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createCompetition() with judge
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION)
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getFullJson(competitionBean))).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createCompetition() with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION)
		                                      .header(Token.TOKEN_HEADER, adminToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getFullJson(competitionBean))).andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void checkGetCompetitionById () throws Exception {
		// try access to getCompetitionById with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to getCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to getCompetitionById with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, adminToken))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testingCompetition.getName()));
	}

	@Test
	public void checkDeleteCompetitionById () throws Exception {
		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID,
				testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to deleteCompetitionById with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID,
				testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, adminToken))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testingCompetition.getName()));
	}

	@Test
	public void checkUpdateCompetitionById () throws Exception {
		Competition test = competitionRepository.findByName(testingCompetition.getName());
		test.setName("Update Name").setLocation("cave number 2");
		// try access to updateCompetition with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to updateCompetition with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to updateCompetition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITION.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(test.getName()));
	}

	@Test
	public void checkGetCount () throws Exception {
		// try access to getCount with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to getCount with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to getCount with authorized admin
		MockHttpServletResponse response =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse();
		assertEquals(response.getContentAsString(), String.valueOf(competitionRepository.count()));
	}

	@Test
	public void checkGetAllCompetitions () throws Exception {
		// try access to getAllCompetitions with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITIONS))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to getAllCompetitions with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITIONS).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to getAllCompetitions with authorized admin
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITIONS).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
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
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                       .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getCompetitionsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                       .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access getCompetitionsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                 ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                                             .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
		                                                            .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<Competition> list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());
		// try to access getCompetitionsByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                       ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1))
		                                                                                                                   .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(30)))
		                                                  .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(20, list.size());
		// try to access to getCompetitionsByPage header with admin
		int sizeAllUser = competitionRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                              ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(page))
		                                                                                                                                          .replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(size)))
		                                                                         .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(countPages));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(page));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(sizeAllUser));
	}

	private void createCompetition (int count) {
		for(int i = 0; i < count; i++) {
			var competition = new Competition().setName("Test firstname" + i);
			competitionRepository.save(competition);
			log.info("Competition %s has been created", competition.getName());
		}
	}

	@Test
	public void checkGetStagesFromCompetitionById () throws Exception {
		List<Stage> stages = testingCompetition.getStages();
		Stage first_blood = new Stage().setName("first blood").setTargets(5).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		stages.add(first_blood);
		testingCompetition = competitionRepository.save(this.testingCompetition.setStages(stages));
		//try access to getStagesFromCompetitionById with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getStagesFromCompetitionById with non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getStagesFromCompetitionById with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Stage> listFromJson = JacksonUtils.getListFromJson(Stage[].class, contentAsString);
		assertEquals(stages.size(), listFromJson.size());
		for(int i = 0; i < listFromJson.size(); i++) {
			testRequiredFields(listFromJson.get(i), stages.get(i));
		}
	}

	@Test
	public void checkPostStages () throws Exception {
		Stage blood1 = new Stage().setName("blood").setTargets(5).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood2 = new Stage().setName("blood").setTargets(4).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood3 = new Stage().setName("blood").setTargets(3).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood4 = new Stage().setName("blood").setTargets(2).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		List<Stage> setupList = List.of(blood1, blood2, blood3, blood4);
		String fullJson = JacksonUtils.getJson(setupList);
		//try to access postStages with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(fullJson))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try to access postStages with user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(fullJson))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try to access postStages with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(fullJson)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Stage> listFromJson = JacksonUtils.getListFromJson(Stage[].class, contentAsString);
		assertEquals(4, listFromJson.size());
		for(int i = 0; i < listFromJson.size(); i++) {
			testRequiredFields(listFromJson.get(i), setupList.get(i));
		}
	}

	@Test
	public void checkPostStage () throws Exception {
		List<Stage> stages = competitionRepository.findById(testingCompetition.getId()).get().getStages();
		//try access to postStage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .content(stageJson)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to postStage with user role
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .content(stageJson)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to postStage with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(stageJson)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Stage stage = JacksonUtils.fromJson(Stage.class, contentAsString);
		assertEquals(stages.size() + 1, competitionRepository.findById(testingCompetition.getId()).get().getStages().size());
		testRequiredFields(stage, testingStage);
	}

	@Test
	public void checkGetStageById () throws Exception {
		List<Stage> stages = testingCompetition.getStages();
		stages.add(testingStage);
		testingCompetition = competitionRepository.save(testingCompetition.setStages(stages));
		Stage stageDB = findStage(testingCompetition, testingStage);
		//try access to getPost with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                         .replace(ControllerAPI.REQUEST_STAGE_ID, stageDB.getId().toString())).contentType(MediaType.APPLICATION_JSON_UTF8))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getPost with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                         .replace(ControllerAPI.REQUEST_STAGE_ID, stageDB.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getPost with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                                  .replace(ControllerAPI.REQUEST_STAGE_ID, stageDB.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		testRequiredFields(Objects.requireNonNull(JacksonUtils.fromJson(Stage.class, contentAsString)), testingStage);
	}

	@Test
	public void checkDeleteStageById () throws Exception {
		Stage removeStage = new Stage().setName("Removed stage").setMaximumPoints(60).setNumberOfRoundToBeScored(12).setTargets(5);
		List<Stage> stages = testingCompetition.getStages();
		stages.add(testingStage);
		stages.add(removeStage);
		testingCompetition = competitionRepository.save(testingCompetition.setStages(stages));
		Stage stageToRemove = findStage(testingCompetition, removeStage);
		//try access to deleteStage with non unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                               .replace(ControllerAPI.REQUEST_STAGE_ID, stageToRemove.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to deleteStage with non admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                               .replace(ControllerAPI.REQUEST_STAGE_ID, stageToRemove.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to deleteStage with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                       ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                                        .replace(ControllerAPI.REQUEST_STAGE_ID, stageToRemove.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(testingCompetition.getStages().size() - 1, competitionRepository.findById(testingCompetition.getId()).get().getStages().size());
		assertTrue(competitionRepository.findById(removeStage.getId()).isEmpty());
	}

	@Test
	public void checkUpdateStage () throws Exception {
		List<Stage> stages = testingCompetition.getStages();
		stages.add(testingStage);
		testingCompetition.setStages(stages);
		Competition save = competitionRepository.save(testingCompetition);
		Stage saveStage = findStage(save, testingStage);
		saveStage.setName("update name");
		//try access to putStage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.REQUEST_STAGE_ID, saveStage.getId().toString())
		                                                                                         .replace(ControllerAPI.REQUEST_COMPETITION_ID, save.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to putStage with non admin role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.REQUEST_STAGE_ID, saveStage.getId().toString())
		                                                                                         .replace(ControllerAPI.REQUEST_COMPETITION_ID, save.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to putStage with admin role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.REQUEST_STAGE_ID, saveStage.getId().toString())
		                                                                                         .replace(ControllerAPI.REQUEST_COMPETITION_ID, save.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value("update name"));
	}

	@Test
	public void checkGetEnum () throws Exception {
		//try access to getEnum from admin user
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
		List<Stage> listFromJson = JacksonUtils.getListFromJson(Stage[].class, contentAsString);
		assertEquals(ClassifierIPSC.getcount(), listFromJson.size());
	}

	@Test
	public void checkGetCompetitors () throws Exception {
		List<Competitor> competitors = testingCompetition.getCompetitors();
		competitors.add(testingCompetitor);
		testingCompetition = competitionRepository.save(testingCompetition.setCompetitors(competitors));
		//try access to getCompetitors with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITORS.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getCompetitors with non admin role
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITORS.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getCompetitors with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITORS.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Competitor> listFromJson = JacksonUtils.getListFromJson(Competitor[].class, contentAsString);
		assertEquals(competitors.size(), listFromJson.size());
	}

	@Test
	public void checkPostCompetitor () throws Exception {
		//try access to postCompetitor with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to postCompetitor with non admin role
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor)))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to postCompetitor with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor))))
		                                .andExpect(MockMvcResultMatchers.status().isCreated())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		Competitor competitor = JacksonUtils.fromJson(Competitor.class, contentAsString);
		assertEquals(testingCompetitor.getName(), competitor.getName());
		assertEquals(testingCompetitor.getRfidCode(), competitor.getRfidCode());
		assertEquals(testingCompetitor.getPerson().getId(), competitor.getPerson().getId());
		assertEquals(testingCompetitor.getPerson().getName(), competitor.getPerson().getName());
	}

	@Test
	public void checkDeleteCompetitor () throws Exception {
		assertEquals(testingCompetition.getCompetitors().size(), 0);
		List<Competitor> competitors = testingCompetition.getCompetitors();
		competitors.add(testingCompetitor);
		testingCompetition = competitionRepository.save(testingCompetition);
		testingCompetitor = testingCompetition.getCompetitors().get(0);
		//try access to deleteCompetitor with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                    .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to deleteCompetitor with non admin
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                    .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to deleteCompetitor with admin
		assertEquals(testingCompetition.getCompetitors().size(), 1);
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                    .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(competitionRepository.findById(testingCompetition.getId()).get().getCompetitors().size(), 0);
	}

	@Test
	public void checkPutCompetitor () throws Exception {
		assertEquals(testingCompetition.getCompetitors().size(), 0);
		List<Competitor> competitors = testingCompetition.getCompetitors();
		competitors.add(testingCompetitor);
		testingCompetition = competitionRepository.save(testingCompetition);
		testingCompetitor = testingCompetition.getCompetitors().get(0).setName("crazy frog");
		//try access to deleteCompetitor with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                              .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to deleteCompetitor with non admin
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                              .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor)))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to deleteCompetitor with admin
		assertEquals(testingCompetition.getCompetitors().size(), 1);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())
		                                                                                                                       .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .content(Objects.requireNonNull(JacksonUtils.getJson(testingCompetitor))))
		                                .andExpect(MockMvcResultMatchers.status().isOk())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		Competitor competitor = JacksonUtils.fromJson(Competitor.class, contentAsString);
		assertEquals(testingCompetitor.getName(), competitor.getName());
		assertEquals(testingCompetitor.getRfidCode(), competitor.getRfidCode());
		assertEquals(testingCompetitor.getPerson().getId(), competitor.getPerson().getId());
		assertEquals(testingCompetitor.getPerson().getName(), competitor.getPerson().getName());
		assertEquals(competitionRepository.findById(testingCompetition.getId()).get().getCompetitors().size(), testingCompetition.getCompetitors().size());
	}

	@Test
	public void checkGetCompetitor () throws Exception {
		List<Competitor> competitors = testingCompetition.getCompetitors();
		competitors.add(testingCompetitor);
		testingCompetition = competitionRepository.save(testingCompetition.setCompetitors(competitors));
		//try access to getCompetitor with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetition.getCompetitors().get(0).getId().toString())
		                                                                                              .replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getCompetitor with non admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetition.getCompetitors().get(0).getId().toString())
		                                                                                              .replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getCompetitor with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITOR_ID,
			                                                                    testingCompetition.getCompetitors().get(0).getId().toString())
		                                                                                                                       .replace(ControllerAPI.REQUEST_COMPETITION_ID, testingCompetition.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Competitor competitor = JacksonUtils.fromJson(Competitor.class, contentAsString);
		assertEquals(testingCompetitor.getName(), competitor.getName());
		assertEquals(testingCompetitor.getRfidCode(), competitor.getRfidCode());
		assertEquals(testingCompetitor.getPerson().getId(), competitor.getPerson().getId());
		assertEquals(testingCompetitor.getPerson().getName(), competitor.getPerson().getName());
	}

	@Test
	public void checkCreateCompetitionWithJudge () throws Exception {
		userRepository.deleteByRoleName(RoleName.JUDGE);
		userRepository.saveAll(List.of(new User().setName("asdfg").setLogin("asdghjkklll").setPassword("tsgaudjscc").setRoleName(RoleName.JUDGE),
			new User().setName("asdjhjhfg").setLogin("asdgjhjhhjkklll").setPassword("tsgagfgudjscc").setRoleName(RoleName.JUDGE)));
		List<User> byRoleName = userRepository.findByRoleName(RoleName.JUDGE);
		competition = new Competition().setName("tryyy").setLocation("kjcxghjcgxhj");
		competition.setMatchDirector(byRoleName.get(0)).setRangeMaster(byRoleName.get(1));
		String fullJson = JacksonUtils.getJson(setupCompetitionBean(competition));
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION)
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(fullJson)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Competition competitionResponse = JacksonUtils.fromJson(Competition.class, contentAsString);
		assertEquals(competitionResponse.getName(), competition.getName());
		assertEquals(competitionResponse.getLocation(), competition.getLocation());
		assertEquals(competitionResponse.getMatchDirector().getLogin(), byRoleName.get(0).getLogin());
		assertEquals(competitionResponse.getRangeMaster().getLogin(), byRoleName.get(1).getLogin());
	}

	@Test
	public void checkGetWeaponEnum () throws Exception {
		//try access to getEnumWeapon from admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON)
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<TypeWeapon> listFromJson = JacksonUtils.getListFromJson(TypeWeapon[].class, contentAsString);
		assertEquals(WeaponTypeEnum.getCount(), listFromJson.size());
	}

	@Test
	public void checkPostsCompetitors () throws Exception {
		Competition competition = competitionRepository.findById(testingCompetition.getId()).get();
		assertEquals(0, competition.getCompetitors().size());
		List<Long> result = createPersons(20);
		assertEquals(20, result.size());
		String json = JacksonUtils.getJson(result);
		assertNotNull(json);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_LIST_COMPETITOR.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(json)
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Competitor[] competitors = JacksonUtils.fromJson(Competitor[].class, contentAsString);
		assertEquals(20, competitors.length);
		assertEquals(20, competitionRepository.findById(testingCompetition.getId()).get().getCompetitors().size());
	}

	@Test
	public void checkAddedMarkForCompetitor () throws Exception {
		Competition competition = competitionRepository.findById(testingCompetition.getId()).get();
		assertEquals(0, competition.getCompetitors().size());
		competition.getCompetitors().add(testingCompetitor);
		competition = competitionRepository.save(competition);
		assertEquals(1, competition.getCompetitors().size());
		testingCompetitor = competition.getCompetitors().get(0);
		assertNotNull(testingCompetitor);
		CompetitorMark competitorMark = new CompetitorMark().setName("aqua").setActive(true).setType(TypeMarkEnum.RFID).setMark("1032132548798");
		String json = JacksonUtils.getJson(competitorMark);
		assertNotNull(json);
		//try access to addedMarkForCompetitor with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                        .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(json)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to addedMarkForCompetitor with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                        .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(json)
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to addedMarkForCompetitor with judge role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                                                 .replace(ControllerAPI.REQUEST_COMPETITOR_ID,
			                                                                                                                                 testingCompetitor.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(json)
		                                                               .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Competitor competitor = JacksonUtils.fromJson(Competitor.class, contentAsString);
		testing(competitor, competitorMark, testingCompetitor);
		//try access to addedMarkForCompetitor with admin role
		contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                             ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                                          .replace(ControllerAPI.REQUEST_COMPETITOR_ID, testingCompetitor.getId().toString()))
		                                                        .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                        .content(json)
		                                                        .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		competitor = JacksonUtils.fromJson(Competitor.class, contentAsString);
		testing(competitor, competitorMark, testingCompetitor);
	}

	@Test
	public void checkGetLevelEnum () throws Exception {
		//try access to getLevelEnum from unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getLevelEnum from user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getLevelEnum from judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getLevelEnum from admin role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL).header(Token.TOKEN_HEADER, adminToken))
		       .andExpect(MockMvcResultMatchers.status().isOk());
	}

	private void testing (Competitor competitor, CompetitorMark competitorMark, Competitor testingCompetitor) {
		assertNotNull(competitor);
		assertEquals(competitorMark.getName(), competitor.getName());
		if(competitorMark.getType().equals(TypeMarkEnum.RFID)) {
			assertEquals(competitorMark.getMark(), competitor.getRfidCode());
		} else {
			assertEquals(competitorMark.getMark(), competitor.getNumber());
		}
		assertEquals(testingCompetitor.getId(), competitor.getId());
		assertEquals(testingCompetitor.getPerson(), competitor.getPerson());
	}

	private List<Long> createPersons (int count) {
		List<Long> result = new ArrayList<>();
		for(int i = 0; i < count; i++) {
			var user = new Person().setName(RandomStringUtils.randomAlphanumeric(10));
			Person save = personRepository.save(user);
			result.add(save.getId());
			log.info("Person %s has been created", user.getName());
		}
		return result;
	}

	@Test
	public void checkCreateScoreRow () throws Exception {
		//prepare
		Competition competition = competitionRepository.findById(testingCompetition.getId()).get();
		assertEquals(0, competition.getStages().size());
		competition.getStages().add(testingStage);
		assertEquals(0, competition.getCompetitors().size());
		competition.getCompetitors().add(testingCompetitor.setRfidCode("46384672364823648263"));
		competition = competitionRepository.save(competition);
		assertEquals(1, competition.getCompetitors().size());
		assertEquals(1, competition.getStages().size());
		Long competitorId = competition.getCompetitors().get(0).getPerson().getId();
		Long stageId = competition.getStages().get(0).getId();
		ScoreBean scoreBean = new ScoreBean().setType(TypeMarkEnum.RFID).setMark("46384672364823648263").setScore(50).setTimeOfExercise(4564646L);
		//try access to create score with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(scoreBean)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to create score with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, user)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(scoreBean)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to create score with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(scoreBean)))).andExpect(MockMvcResultMatchers.status().isCreated());
		//try access to create score with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                     ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                                    .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .content(Objects.requireNonNull(JacksonUtils.getJson(scoreBean))))
		                                .andExpect(MockMvcResultMatchers.status().isCreated())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		Score score = JacksonUtils.fromJson(Score.class, contentAsString);
		assertEquals(stageId, score.getStageId());
		assertEquals(competitorId, score.getPersonId());
		assertEquals(competitorId, score.getPersonId());
		assertEquals(scoreBean.getDisqualificationReason(), score.getDisqualificationReason());
		assertEquals(scoreBean.getTimeOfExercise(), score.getTimeOfExercise());
		assertEquals(scoreBean.getScore(), score.getScore());
	}

	@Test
	public void checkCreateScoreFromList () throws Exception {
		//prepare
		Competition competition = competitionRepository.findById(testingCompetition.getId()).get();
		assertEquals(0, competition.getStages().size());
		competition.getStages().add(testingStage);
		assertEquals(0, competition.getCompetitors().size());
		List<Competitor> competitors = competition.getCompetitors();
		competitors.add(testingCompetitor.setRfidCode("46384672364823648263"));
		competitors.add(new Competitor().setName("test for2").setPerson(personRepository.save(new Person().setName("test2"))).setNumber("434342342342342"));
		competition.setCompetitors(competitors);
		competition = competitionRepository.save(competition);
		assertEquals(2, competition.getCompetitors().size());
		assertEquals(1, competition.getStages().size());
		Long competitorId = competition.getCompetitors().get(0).getPerson().getId();
		Long stageId = competition.getStages().get(0).getId();
		List<ScoreBean> res = new ArrayList<>();
		res.add(new ScoreBean().setType(TypeMarkEnum.RFID).setMark("46384672364823648263").setScore(50).setTimeOfExercise(4564646L));
		res.add(new ScoreBean().setType(TypeMarkEnum.NUMBER).setMark("434342342342342").setScore(40).setTimeOfExercise(434343434L));
		//try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE_LIST.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(res)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access with  user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE_LIST.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(res)))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access with  judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                            ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE_LIST.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(res)))).andExpect(MockMvcResultMatchers.status().isCreated());
		//try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                     ControllerAPI.COMPETITION_CONTROLLER_POST_SCORE_LIST.replace(ControllerAPI.REQUEST_COMPETITION_ID, competition.getId().toString())
		                                                                                                                         .replace(ControllerAPI.REQUEST_STAGE_ID, stageId.toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .header(Token.TOKEN_HEADER, adminToken)
		                                                               .content(Objects.requireNonNull(JacksonUtils.getJson(res))))
		                                .andExpect(MockMvcResultMatchers.status().isCreated())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		Score[] scores = JacksonUtils.fromJson(Score[].class, contentAsString);
		assertEquals(scores.length, res.size());
	}

	@Test
	public void checkGetTypeMark () throws Exception {
		//try access to getMarkType with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_TYPE_MARK_ENUM))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to getMarkType with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_TYPE_MARK_ENUM).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to getMarkType with judge role
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_TYPE_MARK_ENUM).header(Token.TOKEN_HEADER, judgeToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
		String[] strings = JacksonUtils.fromJson(String[].class, contentAsString);
		assertEquals(TypeMarkEnum.values().length, strings.length);
		//try access to getMarkType with admin role
		contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_TYPE_MARK_ENUM).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
		strings = JacksonUtils.fromJson(String[].class, contentAsString);
		assertEquals(TypeMarkEnum.values().length, strings.length);
	}
}
