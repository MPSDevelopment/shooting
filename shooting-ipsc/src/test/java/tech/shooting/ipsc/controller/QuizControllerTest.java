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
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.QuizService;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = QuizRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
	TokenAuthenticationFilter.class, IpscUserDetailsService.class, QuizController.class, ValidationErrorHandler.class, QuizService.class, DatabaseCreator.class})
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

	@Autowired
	private SubjectRepository subjectRepository;

	private Subject subject;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private String json;

	private QuizBean quizBean;

	private Quiz testQuiz;

	private Question testQuestion;

	private List<Subject> subjectsFromDb;

	@BeforeEach
	void setUp () {
		quizRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		subjectsFromDb = subjectRepository.findAll();
		subjectsFromDb = subjectsFromDb == null ? subjectRepository.createIfNotExists(List.of(
			new Subject().setRus("Огневая подготовка").setKz("Fire training"),
			new Subject().setRus("Физическая подготовка").setKz("PHYSICAL"),
			new Subject().setRus("Военно-медицинская подготовка").setKz("MILITARY_MEDICIAL"),
			new Subject().setRus("Тактико-специальная подготовка").setKz("SPECIAL_TACTICAL"),
			new Subject().setRus("Тактико-служебно боевого применения").setKz("TACTICAL_AND_SERVICE_COMBAT_USE"),
			new Subject().setRus("Высотная подготовка").setKz("ALTITUDE"),
			new Subject().setRus("Общевоинские уставы").setKz("GENERAL_MILITARY_REGULATIONS"),
			new Subject().setRus("Военно-инженерная подготовка").setKz("MILITARY_ENGINEERING"),
			new Subject().setRus("Радиационная, химическая и биологическая защита").setKz("RADIATION_CHEMICAL_AND_BIOLOGICAL_PROTECTION"),
			new Subject().setRus("Подготовка по связи").setKz("COMMUNICATION"),
			new Subject().setRus("Военная топография").setKz("MILITARY_TOPOGRAPHY"),
			new Subject().setRus("Специальная подготовка по категориям специалистов").setKz("SPECIAL_TRAINING_IN_PROFESSIONAL_CATEGORIES"))) : subjectsFromDb;
		subject = subjectsFromDb.get(0);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		quizBean = new QuizBean().setName(new QuizName().setKz("Examination of weapon handling").setRus("балалайка мишка пляс ... ")).setSubject(subject).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L);
		testQuiz = quizRepository.save(new Quiz().setName(new QuizName().setKz("Examination of weapon handling").setRus("балалайка мишка пляс ... ")).setSubject(subject)
		                                         .setGreat(90)
		                                         .setGood(70)
		                                         .setSatisfactorily(40)
		                                         .setTime(8000000L));
		testQuestion = new Question().setQuestion(new Ask().setRus(" аврраоврылоарвларвлоыарвлыора").setKz("What is you name"))
		                             .setRandom(false)
		                             .setAnswers(List.of(new Answer().setRus("бояра").setKz("Tom"),
			                             new Answer().setRus("водяра").setKz("Mike"),
			                             new Answer().setRus("даун").setKz("Steven"),
			                             new Answer().setRus("полный ноль").setKz("Undefined")))
		                             .setRight(3);
	}

	@Test
	void checkCreateQuiz () throws Exception {
		//prepare
		json = JacksonUtils.getJson(quizBean);
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
		long count = quizRepository.count();
		//try access to createQuiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ)
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(json)
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Quiz quiz = JacksonUtils.fromJson(Quiz.class, contentAsString);
		assertEquals(count + 1, quizRepository.findAll().size());
		checkQuiz(quizBean, quiz);
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
		Subject[] subjects = JacksonUtils.fromJson(tech.shooting.ipsc.pojo.Subject[].class, contentAsString);
		assertEquals(subjectsFromDb.size(), subjects.length);
		for(int i = 0; i < subjects.length; i++) {
			log.info("Subject is %s", subjects[i]);
		}
	}

	@Test
	public void checkGetAllQuiz () throws Exception {
		//try access to get list quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to get list quiz with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get list quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get list quiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, adminToken))
		                                .andExpect(MockMvcResultMatchers.status().isOk())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		assertEquals(quizRepository.findAll().size(), JacksonUtils.fromJson(Quiz[].class, contentAsString).length);
	}

	@Test
	void checkFindBySubject () throws Exception {
		//try access to get quiz by subject
		//used unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT, subject.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//used user role
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT, subject.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//used judge role
		mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT, subject.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//used admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT, subject.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		Quiz[] quizzes = JacksonUtils.fromJson(Quiz[].class, contentAsString);
		for(int i = 0; i < quizzes.length; i++) {
			assertEquals(quizzes[i].getSubject().getId(), subject.getId());
		}
	}

	@Test
	public void checkGetQuiz () throws Exception {
		//try access to get quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to get quiz with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to get quiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
		                                .andExpect(MockMvcResultMatchers.status().isOk())
		                                .andReturn()
		                                .getResponse()
		                                .getContentAsString();
		assertEquals(testQuiz, JacksonUtils.fromJson(Quiz.class, contentAsString));
	}

	@Test
	public void checkUpdateQuiz () throws Exception {
		//prepare
		json = JacksonUtils.getJson(testQuiz.setTime(200L));
		//try access to update quiz
		// with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, judgeToken)
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isForbidden());
		//with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(Objects.requireNonNull(json))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(testQuiz, JacksonUtils.fromJson(Quiz.class, contentAsString));
	}

	@Test
	public void checkDeleteQuiz () throws Exception {
		//try access to remove quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//try access to remove quiz with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to remove quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//try access to remove quiz with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(quizRepository.findById(testQuiz.getId()), Optional.empty());
	}

	@Test
	public void checkCreateQuestion () throws Exception {
		//try access to added question to exist quiz
		//unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//user role
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion)))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//judge role
		mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion)))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(
			ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion)))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		checkQuestion(testQuestion, JacksonUtils.fromJson(Question.class, contentAsString));
	}

	private void checkQuestion (Question testQuestion, Question fromJson) {
		assertEquals(testQuestion.getQuestion(), fromJson.getQuestion());
		assertEquals(testQuestion.getRight(), fromJson.getRight());
		assertEquals(testQuestion.getAnswers(), fromJson.getAnswers());
		assertEquals(testQuestion.isRandom(), fromJson.isRandom());
	}

	@Test
	public void checkGetQuestion () throws Exception {
		//try access to get question by id
		List<Question> questionList = testQuiz.getQuestionList();
		questionList.add(testQuestion);
		testQuiz.setQuestionList(questionList);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		//unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                                              .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Question fromJson = JacksonUtils.fromJson(Question.class, contentAsString);
		checkQuestion(question, fromJson);
		assertEquals(question.getId(), fromJson.getId());
	}

	@Test
	public void checkRemoveQuestion () throws Exception {
		//try access to remove question
		testQuiz.getQuestionList().add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		long count = quizRepository.findById(save.getId()).get().getQuestionList().size();
		//unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())).header(Token.TOKEN_HEADER, userToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())).header(Token.TOKEN_HEADER, judgeToken))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		//admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                              ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                           .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
		       .andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(count - 1, quizRepository.findById(save.getId()).get().getQuestionList().size());
	}

	@Test
	public void checkUpdate () throws Exception {
		//try access to update question
		testQuiz.getQuestionList().add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		question.setRight(1);
		long count = quizRepository.findById(save.getId()).get().getQuestionList().size();
		//unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getJson(question))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		//user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getJson(question))
		                                      .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                           ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                     .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
		                                      .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                      .content(JacksonUtils.getJson(question))
		                                      .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		//admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 +
		                                                                    ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())
		                                                                                                              .replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
		                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
		                                                               .content(JacksonUtils.getJson(question))
		                                                               .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		checkQuestion(question, JacksonUtils.fromJson(Question.class, contentAsString));
	}
}