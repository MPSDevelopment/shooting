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
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.Subject;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.QuizService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = QuizRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, QuizController.class, ValidationErrorHandler.class, QuizService.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class QuizControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuizRepository quizRepository;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private String json;

	@BeforeEach
	void setUp () {
		quizRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void checkCreateQuiz () throws Exception {
		//prepare
		QuizBean fromFront =
			new QuizBean().setName(new QuizName().setKz("Examination of weapon handling").setRus("балалайка мишка пляс ... ")).setSubject(Subject.FIRE).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L);
		json = JacksonUtils.getJson(fromFront);
		//try access to createQuiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to createQuiz with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(json)
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to createQuiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(json)
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to createQuiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(json)
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Quiz quiz = JacksonUtils.fromJson(Quiz.class, contentAsString);
		assertEquals(1, quizRepository.findAll().size());
		checkQuiz(fromFront, quiz);
	}

	private void checkQuiz (QuizBean fromFront, Quiz quiz) {
		assertEquals(fromFront.getName(), quiz.getName());
		assertEquals(fromFront.getSubject(), quiz.getSubject());
		assertEquals(fromFront.getGreat(), quiz.getGreat());
		assertEquals(fromFront.getGood(), quiz.getGood());
		assertEquals(fromFront.getSatisfactorily(), quiz.getSatisfactorily());
		assertEquals(fromFront.getTime(), quiz.getTime());
	}

	@Test
	public void checkGetEnumSubjects () throws Exception {
		//try access to get subjects enum with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to get subjects enum with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get subjects enum with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get subjects enum with admin role
		String contentAsString =
			mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, adminToken))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getContentAsString();
		assertEquals(Subject.getList().size(), JacksonUtils.fromJson(SubjectsName[].class, contentAsString).length);
	}
}