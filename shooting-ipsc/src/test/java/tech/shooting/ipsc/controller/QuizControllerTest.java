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
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.*;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.QuizScoreRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.QuizService;
import tech.shooting.ipsc.service.WorkspaceService;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = QuizRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, QuizController.class, QuizService.class })
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
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuizScoreRepository quizScoreRepository;

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

	private User guest;

	private String guestToken;

	private Person anotherPerson;

	private Person testingPerson;

	@BeforeEach
	void setUp() {
		quizRepository.deleteAll();
		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		guest = userRepository.findByLogin(DatabaseCreator.GUEST_LOGIN);
		subjectsFromDb = subjectRepository.findAll();
		subject = subjectsFromDb.get(0);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		guestToken = tokenUtils.createToken(guest.getId(), Token.TokenType.USER, guest.getLogin(), RoleName.GUEST, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		quizBean = new QuizBean().setName(new QuizName().setKz("Examination of weapon handling").setRus("?????????????????? ?????????? ???????? ... ")).setSubject(subject.getId()).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L);
		testQuiz = quizRepository.save(new Quiz().setName(new QuizName().setKz("Examination of weapon handling").setRus("?????????????????? ?????????? ???????? ... ")).setSubject(subject).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L));
		testQuestion = new Question().setQuestion(new Ask().setRus("What is your name").setKz("What is you name")).setRandom(false)
				.setAnswers(List.of(new Answer().setRus("??????????").setKz("Tom"), new Answer().setRus("????????????").setKz("Mike"), new Answer().setRus("????????").setKz("Steven"), new Answer().setRus("???????????? ????????").setKz("Undefined"))).setRight(3);

		testingPerson = personRepository.save(new Person().setName("testing person"));
		anotherPerson = personRepository.save(new Person().setName("another person"));

	}

	@Test
	void checkCreateQuiz() throws Exception {
		// prepare
		json = JacksonUtils.getJson(quizBean);
		// try access to createQuiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to createQuiz with user role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createQuiz with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON).content(json).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to createQuiz with user role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON).content(json).header(Token.TOKEN_HEADER, guestToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		long count = quizRepository.count();
		// try access to createQuiz with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ).contentType(MediaType.APPLICATION_JSON).content(json).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Quiz quiz = JacksonUtils.fromJson(Quiz.class, contentAsString);
		assertEquals(count + 1, quizRepository.findAll().size());
		checkQuiz(quizBean, quiz);
	}

	private void checkQuiz(QuizBean fromFront, Quiz quiz) {
		assertEquals(fromFront.getName(), quiz.getName());
		assertEquals(fromFront.getSubject(), quiz.getSubject().getId());
		assertEquals(fromFront.getGreat(), quiz.getGreat());
		assertEquals(fromFront.getGood(), quiz.getGood());
		assertEquals(fromFront.getSatisfactorily(), quiz.getSatisfactorily());
		assertEquals(fromFront.getTime(), quiz.getTime());
	}

	@Test
	public void checkGetEnumSubjects() throws Exception {
		// try access to get subjects enum with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to get subjects enum with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get subjects enum with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to get subjects enum with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, guestToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get subjects enum with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Subject[] subjects = JacksonUtils.fromJson(tech.shooting.ipsc.pojo.Subject[].class, contentAsString);
		assertEquals(subjectsFromDb.size(), subjects.length);
		for (int i = 0; i < subjects.length; i++) {
			log.info("Subject is %s", subjects[i]);
		}
	}

	@Test
	public void checkGetAllQuiz() throws Exception {
		// try access to get list quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get list quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// guest role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, guestToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get list quiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(quizRepository.findAll().size(), JacksonUtils.fromJson(Quiz[].class, contentAsString).length);
	}

	@Test
	void checkFindBySubject() throws Exception {
		// try access to get quiz by subject
		// used unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT_ID, subject.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// used user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT_ID, subject.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// used guest role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT_ID, subject.getId().toString()))
				.header(Token.TOKEN_HEADER, guestToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// used judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT_ID, subject.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// used admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ.replace(ControllerAPI.REQUEST_SUBJECT_ID, subject.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertNotNull(contentAsString);
		Quiz[] quizzes = JacksonUtils.fromJson(Quiz[].class, contentAsString);
		for (int i = 0; i < quizzes.length; i++) {
			assertEquals(quizzes[i].getSubject().getId(), subject.getId());
		}
	}

	@Test
	public void checkGetQuiz() throws Exception {
		// try access to get quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to get quiz with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to get quiz with guest role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, guestToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access to get quiz with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(testQuiz, JacksonUtils.fromJson(Quiz.class, contentAsString));
	}

	@Test
	public void checkUpdateQuiz() throws Exception {
		// prepare
		testQuiz.setTime(200L);
		BeanUtils.copyProperties(testQuiz, quizBean);
		quizBean.setSubject(testQuiz.getSubject().getId());
		json = JacksonUtils.getJson(quizBean);
		// try access to update quiz
		// with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isOk());
		// with guest role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, guestToken).contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isOk());
		// with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(json))).andExpect(MockMvcResultMatchers.status().isForbidden());
		// with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(json)).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(testQuiz, JacksonUtils.fromJson(Quiz.class, contentAsString));
	}

	@Test
	public void checkDeleteQuiz() throws Exception {
		// try access to remove quiz with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to remove quiz with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to remove quiz with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to remove quiz with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(quizRepository.findById(testQuiz.getId()), Optional.empty());
	}

	@Test
	public void checkCreateQuestion() throws Exception {
		// try access to added question to exist quiz
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion))).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON).content(Objects.requireNonNull(JacksonUtils.getJson(testQuestion))).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		checkQuestion(testQuestion, JacksonUtils.fromJson(Question.class, contentAsString));
	}

	private void checkQuestion(Question testQuestion, Question fromJson) {
		assertEquals(testQuestion.getQuestion(), fromJson.getQuestion());
		assertEquals(testQuestion.getAnswers(), fromJson.getAnswers());
		assertEquals(testQuestion.isRandom(), fromJson.isRandom());
	}

	@Test
	public void checkGetQuestion() throws Exception {
		// try access to get question by id
		List<Question> questionList = testQuiz.getQuestionList();
		questionList.add(testQuestion);
		testQuiz.setQuestionList(questionList);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// guest role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, guestToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Question fromJson = JacksonUtils.fromJson(Question.class, contentAsString);
		checkQuestion(question, fromJson);
		assertEquals(question.getId(), fromJson.getId());
	}

	@Test
	public void checkRemoveQuestion() throws Exception {
		// try access to remove question
		testQuiz.getQuestionList().add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		long count = quizRepository.findById(save.getId()).get().getQuestionList().size();
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(count - 1, quizRepository.findById(save.getId()).get().getQuestionList().size());
	}

	@Test
	public void checkUpdate() throws Exception {
		// try access to update question
		testQuiz.getQuestionList().add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		Question question = save.getQuestionList().get(0);
		question.setRight(1);
		long count = quizRepository.findById(save.getId()).get().getQuestionList().size();
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders
				.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(question))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders
				.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(question)).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders
				.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(question)).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// guest role
		mockMvc.perform(MockMvcRequestBuilders
				.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(question)).header(Token.TOKEN_HEADER, guestToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders
						.put(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, testQuiz.getId().toString()).replace(ControllerAPI.REQUEST_QUESTION_ID, question.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.getJson(question)).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		checkQuestion(question, JacksonUtils.fromJson(Question.class, contentAsString));
	}

	@Test
	void checkPostScore() throws Exception {
		// get person who pass test
		List<Person> all = personRepository.findAll();
		log.info("Person list size is %s", all.size());
		Person testPerson;

		testPerson = personRepository.save(new Person().setName("testing"));

		// get quiz
		log.info("Test quiz is %s", testQuiz);
		// get list question from quiz
		List<Question> questionList = testQuiz.getQuestionList();
		// get question
		questionList.add(testQuestion.setRight(0).setActive(true));
		questionList.add(testQuestion.setRight(1).setActive(true));
		questionList.add(testQuestion.setRight(2).setActive(true));
		questionList.add(testQuestion.setRight(3).setActive(true));
		log.info("List Question is %s", questionList);
		log.info("size is %s", questionList.size());
		// save quiz
		Quiz save = quizRepository.save(testQuiz.setQuestionList(questionList));
		// get question
		List<Question> questionList1 = save.getQuestionList();
		for (int i = 0; i < questionList1.size(); i++) {
			log.info("Question %s is %s", i, questionList1.get(i));
		}
		// create ReportBean
		QuizScoreBean reportBean = new QuizScoreBean().setPerson(testPerson.getId()).setQuizId(save.getId());
		List<RowBean> rowBeans = new ArrayList<>();
		rowBeans.add(new RowBean().setAnswer(3L).setQuestionId(questionList1.get(0).getId()));
		rowBeans.add(new RowBean().setAnswer(2L).setQuestionId(questionList1.get(1).getId()));
		rowBeans.add(new RowBean().setAnswer(0L).setQuestionId(questionList1.get(3).getId()));
		reportBean.setList(rowBeans);
		List<QuizScoreBean> reportBeans = new ArrayList<>();
		reportBeans.add(reportBean);
		String json = JacksonUtils.getJson(reportBeans);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_ANSWER_TO_QUIZ).contentType(MediaType.APPLICATION_JSON)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		log.info("Content is %s", contentAsString);
		List<QuizScore> listFromJson = JacksonUtils.getListFromJson(QuizScore[].class, contentAsString);
		log.info("List is %s", listFromJson);
		log.info("Incorrect size is %s", listFromJson.get(0).getIncorrect());
		log.info("Skip size is %s", listFromJson.get(0).getSkip());
	}

	@Test
	void checkGetQuizToCheck() throws Exception {
		List<Question> questionList = testQuiz.getQuestionList();
		questionList.add(testQuestion);
		testQuestion = new Question().setQuestion(new Ask().setRus(" ??????????????????????????????????????????????????????????").setKz("What is you name")).setRandom(false).setActive(true)
				.setAnswers(List.of(new Answer().setRus("??????????").setKz("Tom"), new Answer().setRus("????????????").setKz("Mike"), new Answer().setRus("????????").setKz("Steven"), new Answer().setRus("???????????? ????????").setKz("Undefined"))).setRight(3);
		questionList.add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		List<Question> collect = save.getQuestionList().stream().filter(Question::isActive).collect(Collectors.toList());
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// user role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<QuestionBean> listFromJson = JacksonUtils.getListFromJson(QuestionBean[].class, contentAsString);
		assertEquals(collect.size(), listFromJson.size());
		for (int i = 0; i < collect.size(); i++) {
			checkQuestionBean(collect.get(i), listFromJson.get(i));
		}
		// admin role
		contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		listFromJson = JacksonUtils.getListFromJson(QuestionBean[].class, contentAsString);
		assertEquals(collect.size(), listFromJson.size());
		for (int i = 0; i < collect.size(); i++) {
			checkQuestionBean(collect.get(i), listFromJson.get(i));
		}
	}

	@Test
	void checkGetQuizByIdAndReturnListQuestion() throws Exception {
		List<Question> questionList = testQuiz.getQuestionList();
		questionList.add(testQuestion);
		testQuestion = new Question().setQuestion(new Ask().setRus(" ??????????????????????????????????????????????????????????").setKz("What is you name")).setRandom(false).setActive(true)
				.setAnswers(List.of(new Answer().setRus("??????????").setKz("Tom"), new Answer().setRus("????????????").setKz("Mike"), new Answer().setRus("????????").setKz("Steven"), new Answer().setRus("???????????? ????????").setKz("Undefined"))).setRight(3);
		questionList.add(testQuestion);
		Quiz save = quizRepository.save(testQuiz);
		List<Question> collect = save.getQuestionList();
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// user role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString())).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Question> listFromJson = JacksonUtils.getListFromJson(Question[].class, contentAsString);
		assertEquals(collect.size(), listFromJson.size());
		for (int i = 0; i < collect.size(); i++) {
			checkQuestion(collect.get(i), listFromJson.get(i));
		}
		// admin role
		contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION.replace(ControllerAPI.REQUEST_QUIZ_ID, save.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		listFromJson = JacksonUtils.getListFromJson(Question[].class, contentAsString);
		assertEquals(collect.size(), listFromJson.size());
		for (int i = 0; i < collect.size(); i++) {
			checkQuestion(collect.get(i), listFromJson.get(i));
		}
	}

	private void checkQuestionBean(Question testQuestion, QuestionBean fromJson) {
		assertNotNull(testQuestion.getRight());
		assertEquals(testQuestion.getId(), fromJson.getId());
		assertEquals(testQuestion.getQuestion(), fromJson.getQuestion());
		assertEquals(testQuestion.getAnswers(), fromJson.getAnswers());
		assertEquals(testQuestion.isRandom(), fromJson.isRandom());
	}

	@Test
	public void checkGetQuizByPage() throws Exception {
		createQuiz(40);
		// try to access getCompetitionsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(
				MockMvcRequestBuilders
						.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// guest role
		mockMvc.perform(
				MockMvcRequestBuilders
						.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, guestToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getCompetitionsByPage with admin user
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders
						.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<Quiz> list = JacksonUtils.getListFromJson(Quiz[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());
		// try to access getCompetitionsByPage with admin user with size 30
		mvcResult = mockMvc
				.perform(MockMvcRequestBuilders
						.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(30)))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		list = JacksonUtils.getListFromJson(Quiz[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(20, list.size());
		// try to access to getCompetitionsByPage header with admin
		int sizeAllUser = quizRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(page)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(size)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(countPages));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(page));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(sizeAllUser));
	}

	@Test
	void checkGetScoreQueryList() throws Exception {

		testQuiz = quizRepository.save(testQuiz);
		quizScoreRepository.save(new QuizScore().setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(4));
		quizScoreRepository.save(new QuizScore().setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(3));
		quizScoreRepository.save(new QuizScore().setPerson(anotherPerson).setQuizId(testQuiz.getId()).setScore(3));

		var query = new QuizScoreRequest();
		query.setPersonId(testingPerson.getId());

		// try access with user role
		String content = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(QuizScore[].class, content);
		assertEquals(2, list.size());

		query.setPersonId(anotherPerson.getId());

		content = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(QuizScore[].class, content);
		assertEquals(1, list.size());
	}

	@Test
	void checkGetScoreQueryListByPage() throws Exception {

		testQuiz = quizRepository.save(testQuiz);
		for (int i = 0; i < 40; i++) {
			quizScoreRepository.save(new QuizScore().setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(4));
			quizScoreRepository.save(new QuizScore().setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(3));
			quizScoreRepository.save(new QuizScore().setPerson(anotherPerson).setQuizId(testQuiz.getId()).setScore(3));
		}

		var query = new QuizScoreRequest();
		query.setPersonId(testingPerson.getId());

		// try access with user role
		String content = mockMvc
				.perform(MockMvcRequestBuilders
						.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(10)))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(QuizScore[].class, content);
		assertEquals(10, list.size());

		query.setPersonId(anotherPerson.getId());

		content = mockMvc
				.perform(MockMvcRequestBuilders
						.post(ControllerAPI.QUIZ_CONTROLLER + ControllerAPI.VERSION_1_0
								+ ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(30)))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(QuizScore[].class, content);
		assertEquals(20, list.size());
	}

//	

	private void createQuiz(int count) {
		for (int i = 0; i < count; i++) {
			Quiz quiz = new Quiz().setName(new QuizName().setKz("Examination of weapon handling").setRus("?????????????????? ?????????? ???????? ... ")).setSubject(subject).setGreat(90).setGood(70).setSatisfactorily(40).setTime(8000000L);
			quizRepository.save(quiz);
			log.info("Quiz %s has been created", quiz.getName());
		}
	}

}