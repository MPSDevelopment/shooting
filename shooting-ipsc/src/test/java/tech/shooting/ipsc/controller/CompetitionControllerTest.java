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
import tech.shooting.ipsc.pojo.Stage;
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

	private Competition competition;
	private Competition testingCompetition;

	private Stage testingStage;
	private String stageJson;

	private String adminToken;


	private String userToken;
	private List<Stage> stages;

	@BeforeEach
	public void before () {
		competitionRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		competition = new Competition().setName("Alladin").setLocation("Cave!");
		testingCompetition = competitionRepository.save(new Competition().setName("Test name Competition"));

		testingStage = new Stage().setNameOfStage("Testing testingStage").setTargets(20).setNumberOfRoundToBeScored(5).setMaximumPoints(25);
		stageJson = JacksonUtils.getJson(testingStage);

		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);


		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

	}

	//utils method's
	private Stage findStage (Competition competition, Stage testingStage) {
		return competition.getStages()
			       .stream()
			       .filter((stage) -> stage.getNameOfStage().equals(testingStage.getNameOfStage()) && stage.getNumberOfRoundToBeScored().equals(testingStage.getNumberOfRoundToBeScored()) &&
			                          stage.getTargets().equals(testingStage.getTargets()) && stage.getMaximumPoints().equals(testingStage.getMaximumPoints()))
			       .findAny()
			       .get();
	}

	private void testRequiredFields (Stage stageFromResponse, Stage testingStage) {
		assertEquals(stageFromResponse.getTargets(), testingStage.getTargets());
		assertEquals(stageFromResponse.getNameOfStage(), testingStage.getNameOfStage());
		assertEquals(stageFromResponse.getMaximumPoints(), testingStage.getMaximumPoints());
		assertEquals(stageFromResponse.getNumberOfRoundToBeScored(), testingStage.getNumberOfRoundToBeScored());
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
			                .content(JacksonUtils.getFullJson(competition))).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(competition.getName()));

	}

	@Test
	public void checkGetCompetitionById () throws Exception {

		// try access to getCompetitionById with unauthorized user
		mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to getCompetitionById with authorized user
		mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to getCompetitionById with authorized admin
		mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testingCompetition.getName()));

	}

	@Test
	public void checkDeleteCompetitionById () throws Exception {

		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to deleteCompetitionById with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
			                .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to deleteCompetitionById with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
			                .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testingCompetition.getName()));

	}

	@Test
	public void checkUpdateCompetitionById () throws Exception {
		Competition test = competitionRepository.findByName(testingCompetition.getName());
		test.setName("Update Name").setLocation("cave number 2");

		// try access to updateCompetition with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, test.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to updateCompetition with authorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, test.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(JacksonUtils.getFullJson(test)))
			                .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to updateCompetition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.COMPETITION_ID_REQUEST, test.getId().toString()))
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
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS).header(Token.TOKEN_HEADER, adminToken))
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
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1)).replace("{pageSize}", String.valueOf(5))))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access getCompetitionsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1)).replace("{pageSize}", String.valueOf(5)))
			                .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access getCompetitionsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1)).replace("{pageSize}", String.valueOf(5)))
			                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		List<Competition> list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());

		// try to access getCompetitionsByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber}", String.valueOf(1)).replace("{pageSize" + "}", String.valueOf(30)))
			                            .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		list = JacksonUtils.getListFromJson(Competition[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(20, list.size());

		// try to access to getCompetitionsByPage header with admin
		int sizeAllUser = competitionRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE.replace("{pageNumber" + "}", String.valueOf(page)).replace("{pageSize}", String.valueOf(size)))
			                                                   .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();

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

	@Test
	public void checkGetStagesFromCompetitionById () throws Exception {
		List<Stage> stages = testingCompetition.getStages();
		Stage first_blood = new Stage().setNameOfStage("first blood").setTargets(5).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		stages.add(first_blood);
		testingCompetition = competitionRepository.save(this.testingCompetition.setStages(stages));

		//try access to getStagesFromCompetitionById with unauthorized user
		mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST,
				testingCompetition.getId().toString())))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try access to getStagesFromCompetitionById with non admin user
		mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());


		//try access to getStagesFromCompetitionById with admin user
		String contentAsString = mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Stage> listFromJson = JacksonUtils.getListFromJson(Stage[].class, contentAsString);
		assertEquals(stages.size(), listFromJson.size());
		for(int i = 0; i < listFromJson.size(); i++) {
			testRequiredFields(listFromJson.get(i), stages.get(i));
		}
	}

	@Test
	public void checkPostStages () throws Exception {
		Stage blood1 = new Stage().setNameOfStage("blood").setTargets(5).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood2 = new Stage().setNameOfStage("blood").setTargets(4).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood3 = new Stage().setNameOfStage("blood").setTargets(3).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		Stage blood4 = new Stage().setNameOfStage("blood").setTargets(2).setNumberOfRoundToBeScored(6).setMaximumPoints(30);
		List<Stage> setupList = List.of(blood1, blood2, blood3, blood4);
		String fullJson = JacksonUtils.getFullJson(setupList);

		//try to access postStages with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(fullJson))).andExpect(MockMvcResultMatchers.status().isUnauthorized());


		//try to access postStages with user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
			                .header(Token.TOKEN_HEADER, userToken)
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(fullJson))).andExpect(MockMvcResultMatchers.status().isForbidden());

		//try to access postStages with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString()))
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
		mockMvc.perform(
			MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST,
				testingCompetition.getId().toString()))
				.content(stageJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());


		//try access to postStage with user role
		mockMvc.perform(
			MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST,
				testingCompetition.getId().toString()))
				.content(stageJson)
				.header(Token.TOKEN_HEADER, userToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isForbidden());

		//try access to postStage with admin role
		String contentAsString = mockMvc.perform(
			MockMvcRequestBuilders.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST,
				testingCompetition.getId().toString()))
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
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                           .replace(ControllerAPI.STAGE_ID_REQUEST, stageDB.getId().toString())).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try access to getPost with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                           .replace(ControllerAPI.STAGE_ID_REQUEST, stageDB.getId().toString())).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		//try access to getPost with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                                                    .replace(ControllerAPI.STAGE_ID_REQUEST, stageDB.getId().toString())).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken))
			                         .andExpect(MockMvcResultMatchers.status().isOk())
			                         .andReturn()
			                         .getResponse()
			                         .getContentAsString();

		testRequiredFields(Objects.requireNonNull(JacksonUtils.fromJson(Stage.class, contentAsString)), testingStage);
	}

	@Test
	public void checkDeleteStageById () throws Exception {
		Stage removeStage = new Stage().setNameOfStage("Removed stage").setMaximumPoints(60).setNumberOfRoundToBeScored(12).setTargets(5);
		List<Stage> stages = testingCompetition.getStages();
		stages.add(testingStage);
		stages.add(removeStage);
		testingCompetition = competitionRepository.save(testingCompetition.setStages(stages));
		Stage stageToRemove = findStage(testingCompetition, removeStage);

		//try access to deleteStage with non unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                              .replace(ControllerAPI.STAGE_ID_REQUEST, stageToRemove.getId().toString())).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try access to deleteStage with non admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                              .replace(ControllerAPI.STAGE_ID_REQUEST, stageToRemove.getId().toString())).header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		//try access to deleteStage with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                       ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE.replace(ControllerAPI.COMPETITION_ID_REQUEST, testingCompetition.getId().toString())
			                                                                       .replace(ControllerAPI.STAGE_ID_REQUEST, stageToRemove.getId().toString()))
			                                         .header(Token.TOKEN_HEADER, adminToken)
			                                         .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		testRequiredFields(Objects.requireNonNull(JacksonUtils.fromJson(Stage.class, contentAsString)), removeStage);
		assertEquals(testingCompetition.getStages().size() - 1, competitionRepository.findById(testingCompetition.getId()).get().getStages().size());

	}

	@Test
	public void checkUpdateStage () throws Exception {
		List<Stage> stages = testingCompetition.getStages();
		stages.add(testingStage);
		testingCompetition.setStages(stages);
		Competition save = competitionRepository.save(testingCompetition);
		Stage saveStage = findStage(save, testingStage);
		saveStage.setNameOfStage("update name");

		//try access to putStage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.STAGE_ID_REQUEST, saveStage.getId().toString()).replace(ControllerAPI.COMPETITION_ID_REQUEST, save.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try access to putStage with non admin role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.STAGE_ID_REQUEST, saveStage.getId().toString()).replace(ControllerAPI.COMPETITION_ID_REQUEST, save.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))
			                .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		//try access to putStage with admin role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE.replace(ControllerAPI.STAGE_ID_REQUEST, saveStage.getId().toString()).replace(ControllerAPI.COMPETITION_ID_REQUEST, save.getId().toString()))
			                .contentType(MediaType.APPLICATION_JSON_UTF8)
			                .content(Objects.requireNonNull(JacksonUtils.getJson(saveStage)))
			                .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.nameOfStage").value("update name"));
	}

}
