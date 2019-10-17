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
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.CategoriesBean;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.enums.StandardPassEnum;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.*;
import tech.shooting.ipsc.service.StandardService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = StandardRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, StandardController.class, StandardService.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class StandardControllerTest {
	@Autowired
	private StandardService standardService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private StandardScoreRepository standardScoreRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private Category testCategory;

	private Subject testSubject;

	private Standard testStandard;

	private Person testingPerson;
	private Person anotherPerson;

	@BeforeEach
	public void before() {
		standardRepository.deleteAll();

		testCategory = new Category().setNameCategoryRus("Vodka").setNameCategoryKz("Weapon Training");
		testSubject = testSubject == null ? subjectRepository.save(new Subject().setRus("LitrBooool").setKz("Physical training")) : testSubject;
		testStandard = new Standard().setActive(true).setSubject(testSubject).setGroups(false)
				.setInfo(new Info().setNamedRus("Бег с припятствиями за водкой").setNamedKz("Running with obstacles").setDescriptionRus("Бла бла бла бла бла").setDescriptionKz(
						"Возраст. Спортсмен может получить определенный разряд только при условии достижения им определенного возраста: с 10 лет — 1-3 юношеские разряды и взрослые разряды, с 14 лет — КМС, с 15 лет — МС, а с 16 лет — МСМК."));
		testingPerson = personRepository.save(new Person().setName("testing person"));
		anotherPerson = personRepository.save(new Person().setName("another person"));

		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void checkGetAllStandard() throws Exception {
		assertEquals(Collections.emptyList(), standardRepository.findAll());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		List<Standard> listFromJson = JacksonUtils.getListFromJson(Standard[].class, contentAsString);
		assertEquals(Collections.EMPTY_LIST, listFromJson);

	}

	@Test
	void checkGetStandardById() throws Exception {
		assertEquals(Collections.emptyList(), standardRepository.findAll());
		int count = standardRepository.findAll().size();
		Standard save = standardRepository.save(testStandard);
		assertEquals(count + 1, standardRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Standard standard = JacksonUtils.fromJson(Standard.class, contentAsString);
		assertEquals(save, standard);

	}

	@Test
	void checkGetStandardByIdWithIncorrectValue() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, "34342343243243243243"))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void checkGetStandardsBySubject() throws Exception {
		Standard save = standardRepository.save(testStandard);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT.replace(ControllerAPI.REQUEST_SUBJECT_ID, save.getSubject().getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT.replace(ControllerAPI.REQUEST_SUBJECT_ID, save.getSubject().getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT.replace(ControllerAPI.REQUEST_SUBJECT_ID, save.getSubject().getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT.replace(ControllerAPI.REQUEST_SUBJECT_ID, save.getSubject().getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Standard> standard = JacksonUtils.getListFromJson(Standard[].class, contentAsString);
		assertEquals(save, standard.get(0));
	}

	@Test
	void checkGetStandardsBySubjectWithIncorrectValue() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT.replace(ControllerAPI.REQUEST_SUBJECT_ID, "12e4324343"))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void checkPostStandard() throws Exception {
		StandardBean bean = createStandardBean();
		String json = JacksonUtils.getJson(bean);
		int count = standardRepository.findAll().size();
		// try with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isCreated());
		count++;
		// try with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try with admin role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated());
		assertEquals(count + 1, standardRepository.findAll().size());
	}

	private StandardBean createStandardBean() {
		StandardBean bean = new StandardBean();
		bean.setInfo(testStandard.getInfo()).setSubject(testStandard.getSubject().getId()).setGroups(testStandard.isGroups()).setActive(testStandard.isActive());
		List<CategoryByTime> res = new ArrayList<>();
		res.add(new CategoryByTime().setCategory(testCategory).setExcellentTime(10F).setGoodTime(12F).setSatisfactoryTime(14F));
		testStandard.setCategoriesList(res);
		List<CategoriesBean> categoriesBeans = new ArrayList<>();
		for (int i = 0; i < testStandard.getCategoriesList().size(); i++) {
			CategoryByTime categoriesAndTime = testStandard.getCategoriesList().get(i);
			categoriesBeans.add(new CategoriesBean().setCategory(categoriesAndTime.getCategory()).setExcellentTime(categoriesAndTime.getExcellentTime()).setGoodTime(categoriesAndTime.getGoodTime())
					.setSatisfactoryTime(categoriesAndTime.getSatisfactoryTime()));
		}
		bean.setCategoriesList(categoriesBeans);
		return bean;
	}

	@Test
	void checkPutStandard() throws Exception {
		int count = standardRepository.findAll().size();
		assertEquals(0, count);
		StandardBean bean = createStandardBean();
		Standard standard = standardService.postStandard(bean);
		assertEquals(count + 1, standardRepository.findAll().size());
		bean.setGroups(true);
		String json = JacksonUtils.getJson(bean);
		// try with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(count + 1, standardRepository.findAll().size());
		Standard standard1 = JacksonUtils.fromJson(Standard.class, contentAsString);
		assertEquals(standard.getId(), standard1.getId());
		assertEquals(standard.getCategoriesList(), standard1.getCategoriesList());
		assertEquals(standard.getConditionsList(), standard1.getConditionsList());
		assertEquals(standard.getFailsList(), standard1.getFailsList());
		assertEquals(standard.getInfo(), standard1.getInfo());
		assertEquals(standard.getSubject(), standard1.getSubject());
		assertEquals(standard.isActive(), standard1.isActive());
		assertNotEquals(standard.isGroups(), standard1.isGroups());

	}

	@Test
	void checkPutStandardWithIncorrectValue() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD.replace(ControllerAPI.REQUEST_STANDARD_ID, "243343432432432"))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(JacksonUtils.getJson(null)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void checkDeleteStandardById() throws Exception {
		int actual = standardRepository.findAll().size();
		assertEquals(0, actual);

		Standard save = standardRepository.save(testStandard);
		assertEquals(actual + 1, standardRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(actual, standardRepository.findAll().size());
	}

	@Test
	void checkStandardScore() throws Exception {

		Standard save = standardRepository.save(testStandard);

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_SCORE.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_SCORE.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		StandardScore score = new StandardScore();
		score.setPersonId(testingPerson.getId());
		score.setStandardId(testStandard.getId());
		score.setScore(4);
		score.setTimeOfExercise(23);

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_SCORE.replace(ControllerAPI.REQUEST_STANDARD_ID, save.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(JacksonUtils.getJson(score)).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isCreated());

	}

	@Test
	void checkGetScore() throws Exception {

		Standard standard = standardRepository.save(testStandard);

		StandardScore score = new StandardScore();
		score.setPersonId(testingPerson.getId());
		score.setStandardId(testStandard.getId());
		score.setScore(4);
		score.setTimeOfExercise(23);

		standardScoreRepository.save(score);

		// try access with user role
		String content = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.STANDARD_CONTROLLER_GET_SCORE.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testingPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		StandardScore gotScore = JacksonUtils.fromJson(StandardScore.class, content);

		assertEquals(4, gotScore.getScore());
	}

	@Test
	void checkGetScoreList() throws Exception {

		Standard standard = standardRepository.save(testStandard);

		StandardScore score = new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(4).setTimeOfExercise(23);
		standardScoreRepository.save(score);

		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));

		// try access with user role
		String content = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_LIST.replace(ControllerAPI.REQUEST_STANDARD_ID, standard.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testingPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(StandardScore[].class, content);

		assertEquals(2, list.size());
	}

	@Test
	void checkGetScoreStandardList() throws Exception {

		testStandard = standardRepository.save(testStandard);
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(4).setTimeOfExercise(23));
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));
		standardScoreRepository.save(new StandardScore().setPersonId(anotherPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));

		// try access with user role
		String content = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_STANDARD_LIST.replace(ControllerAPI.REQUEST_STANDARD_ID, testStandard.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(StandardScore[].class, content);

		assertEquals(3, list.size());
	}

	@Test
	void checkGetScorePersonList() throws Exception {

		testStandard = standardRepository.save(testStandard);
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(4).setTimeOfExercise(23));
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));
		standardScoreRepository.save(new StandardScore().setPersonId(anotherPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));

		// try access with user role
		String content = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_PERSON_LIST.replace(ControllerAPI.REQUEST_PERSON_ID, testingPerson.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(StandardScore[].class, content);
		assertEquals(2, list.size());

		content = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_PERSON_LIST.replace(ControllerAPI.REQUEST_PERSON_ID, anotherPerson.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(StandardScore[].class, content);
		assertEquals(1, list.size());
	}

	@Test
	void checkGetScoreQueryList() throws Exception {

		testStandard = standardRepository.save(testStandard);
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(4).setTimeOfExercise(23));
		standardScoreRepository.save(new StandardScore().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));
		standardScoreRepository.save(new StandardScore().setPersonId(anotherPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));
		
		var query = new StandardScoreRequest();
		query.setPersonId(testingPerson.getId());

		// try access with user role
		String content = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST).content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		var list = JacksonUtils.getListFromJson(StandardScore[].class, content);
		assertEquals(2, list.size());
		
		query.setPersonId(anotherPerson.getId());

		content = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST).content(JacksonUtils.getJson(query)).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		list = JacksonUtils.getListFromJson(StandardScore[].class, content);
		assertEquals(1, list.size());
	}

	@Test
	public void checkGetPassEnum() throws Exception {
		// try access to getEnum from admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_PASS_ENUM).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<StandardPassEnum> listFromJson = JacksonUtils.getListFromJson(StandardPassEnum[].class, contentAsString);
		assertEquals(3, listFromJson.size());
	}

	@Test
	public void checkGetUnitEnum() throws Exception {
		// try access to getEnum from admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_UNIT_ENUM).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<UnitEnum> listFromJson = JacksonUtils.getListFromJson(UnitEnum[].class, contentAsString);
		assertEquals(6, listFromJson.size());
	}
}