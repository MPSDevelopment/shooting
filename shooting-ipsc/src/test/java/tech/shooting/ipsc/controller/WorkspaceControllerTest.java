package tech.shooting.ipsc.controller;

import java.util.Date;
import java.util.List;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.WorkspaceBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.mqtt.JsonMqttCallBack;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.QuizName;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.pojo.Workspace;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.repository.WorkSpaceRepository;
import tech.shooting.ipsc.service.WorkspaceService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, WorkspaceController.class, WorkspaceService.class,
		IpscMqttSettings.class, MqttService.class, JsonMqttCallBack.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)

public class WorkspaceControllerTest {

	@Autowired
	private MqttService mqttService;

	@Autowired
	private IpscMqttSettings settings;

	@Autowired
	private WorkspaceService workspaceService;

	@Autowired
	private WorkSpaceRepository workSpaceRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User guest;

	private User admin;

	private User judge;

	private Person testingPerson;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private String guestToken;

	private Quiz testQuiz;

	@BeforeEach
	public void before() {
		workSpaceRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		testingPerson = personRepository.save(new Person().setName("testing testingPerson for competitor"));
		testQuiz = quizRepository.save(new Quiz().setName(new QuizName().setKz("Examination of weapon handling").setRus("балалайка мишка пляс ... ")).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L));

		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		guest = userRepository.findByLogin(DatabaseCreator.GUEST_LOGIN);
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN) == null ? userRepository.save(new User().setLogin("judge").setRoleName(RoleName.JUDGE).setName("judge_name").setPassword(RandomStringUtils.randomAscii(14)))
				: userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		guestToken = tokenUtils.createToken(guest.getId(), Token.TokenType.USER, guest.getLogin(), RoleName.GUEST, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		
		workspaceService.clear();
	}

	@Test
	void checkWorkSpaceRepository() throws Exception {
		assertEquals(0, workSpaceRepository.findAll().size());
		assertNotNull(guestToken);
	}

	@Test
	void checkPutWorkSpace() throws Exception {

		WorkspaceBean bean = new WorkspaceBean();
		bean.setPersonId(testingPerson.getId());
		bean.setQuizId(testQuiz.getId());
		String json = JacksonUtils.getJson(bean);

		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, judgeToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, guestToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, userToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk());
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	void checkGetAll() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(0, list.size());

		workspaceService.createWorkspace("test", "127.0.0.1");

		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(1, list.size());

		// the same clientid
		workspaceService.createWorkspace("test", "127.0.0.1");
		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(Workspace[].class, content);
		assertEquals(1, list.size());

		// the same ip
		workspaceService.createWorkspace("test2", "127.0.0.1");
		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(Workspace[].class, content);
		assertEquals(1, list.size());
	}

	@Test
	void checkGetAllForTest() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST)
				.contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(0, list.size());

		var workspace = workspaceService.createWorkspace("test", "127.0.0.1");

		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(0, list.size());

		workspace.setUseInTest(true);
		workspace.setPersonId(testingPerson.getId());
		workspace.setQuizId(testQuiz.getId());

		WorkspaceBean bean = new WorkspaceBean();
		BeanUtils.copyProperties(workspace, bean);

		workspaceService.updateWorkspace(bean);

		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(1, list.size());

	}

	@Test
	void checkSetUseForTest() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST)
				.contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(0, list.size());

		var workspace = workspaceService.createWorkspace("test", "127.0.0.1");
		workspace.setUseInTest(false);
		workspace.setPersonId(testingPerson.getId());
		workspace.setQuizId(testQuiz.getId());

		workspaceService.putWorkspace(workspace);

		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(0, list.size());
		
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_USE_IN_TEST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		content = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(Workspace[].class, content);

		assertEquals(1, list.size());
	}

	@Test
	void getWorkspace() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken).with(remoteHost("127.0.0.5d")))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		workspaceService.createWorkspace("test3", "127.0.0.10");

		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken).with(remoteHost("127.0.0.8")))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		var content = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.WORKSPACE_CONTROLLER + ControllerAPI.VERSION_1_0).contentType(MediaType.APPLICATION_JSON_UTF8).header(Token.TOKEN_HEADER, adminToken).with(remoteHost("127.0.0.10")))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var workspace = JacksonUtils.fromJson(Workspace.class, content);
		assertNotNull(workspace);
	}

	private static RequestPostProcessor remoteHost(final String remoteHost) {
		return request -> {
			request.setRemoteAddr(remoteHost);
			return request;
		};
	}

}
