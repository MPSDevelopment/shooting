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
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
import tech.shooting.ipsc.bean.CourseBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CourseRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.CourseService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CourseRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CourseController.class, CourseService.class })
class CourseControllerTest {
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private MockMvc mockMvc;

	private Division testDivision;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private Person testing;

	private Division division;

	private Person person;

	private Person otherPerson;

	@BeforeEach
	void setUp() {
		personRepository.deleteAll();
		courseRepository.deleteAll();
		divisionRepository.deleteAll();

		testDivision = testDivision == null ? divisionRepository.save(new Division().setParent(null).setName("Root").setActive(true)) : testDivision;
		testing = personRepository.save(new Person().setName("testing").setQualifierRank(ClassificationBreaks.D).setDivision(testDivision));
		user = user == null
				? userRepository
						.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150")).setPerson(testing))
				: user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

		division = divisionRepository.save(new Division().setName("First division").setParent(null));
		person = personRepository.save(new Person().setName("First person").setDivision(division));
		otherPerson = personRepository.save(new Person().setName("Second person").setDivision(division));

		courseRepository.save(new Course().setOwner(person).setName("Test"));
		courseRepository.save(new Course().setOwner(otherPerson).setName("Test"));
	}

	@Test
	void checkPostCourse() throws Exception {
		createCourse();
	}

	@Test
	void checkGetCourseByDivision() throws Exception {
		Pair<CourseBean, Course> pair = createCourse();
		Course course = pair.getSecond();
		CourseBean bean = pair.getFirst();
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, course.getOwner().getDivision().getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, course.getOwner().getDivision().getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, course.getOwner().getDivision().getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin role
		String contentAsString1 = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, course.getOwner().getDivision().getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Course> listFromJson = JacksonUtils.getListFromJson(Course[].class, contentAsString1);

		assertEquals(course.getId(), listFromJson.get(2).getId());
	}

	private Pair<CourseBean, Course> createCourse() throws Exception {
//		assertEquals(0, courseRepository.findAll().size());

		long count = courseRepository.count();

		CourseBean bean = new CourseBean().setOwner(testing.getId()).setName("bla bla").setImagePath("fdgdsfdsfsdfsd").setAddress("fsdfds").setDate(OffsetDateTime.now());
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_POST_COURSE).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(JacksonUtils.getJson(bean)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Course course = JacksonUtils.fromJson(Course.class, contentAsString);

		assertEquals(count + 1, courseRepository.findAll().size());
		return Pair.of(bean, course);
	}

	@Test
	void checkGetCourseByPerson() throws Exception {
		Pair<CourseBean, Course> pair = createCourse();
		Course course = pair.getSecond();
		CourseBean bean = pair.getFirst();

		String contentAsString1 = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_PERSON.replace(ControllerAPI.REQUEST_PERSON_ID, testing.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Course> listFromJson = JacksonUtils.getListFromJson(Course[].class, contentAsString1);
		assertEquals(course.getId(), listFromJson.get(0).getId());
	}

	@Test
	void checkGetCourseById() throws Exception {
		Pair<CourseBean, Course> pair = createCourse();
		Course course = pair.getSecond();

		assertEquals(3, courseRepository.findAll().size());
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_ID.replace(ControllerAPI.REQUEST_COURSE_ID, course.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_ID.replace(ControllerAPI.REQUEST_COURSE_ID, course.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_ID.replace(ControllerAPI.REQUEST_COURSE_ID, course.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_ID.replace(ControllerAPI.REQUEST_COURSE_ID, course.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Course courseFromDB = JacksonUtils.fromJson(Course.class, contentAsString);
		assertEquals(course.getId(), courseFromDB.getId());
	}

	@Test
	void checkGetAllCourses() throws Exception {
		Pair<CourseBean, Course> course1 = createCourse();
		assertEquals(3, courseRepository.findAll().size());
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_ALL_COURSES)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_ALL_COURSES).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_ALL_COURSES).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_ALL_COURSES).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Course> course = JacksonUtils.getListFromJson(Course[].class, contentAsString);
//		assertEquals(course1.getSecond().getId(), course.get(0).getId());
	}

	@Test
	void checkPutCourse() throws Exception {
		Pair<CourseBean, Course> course = createCourse();
		CourseBean first = course.getFirst().setName("fsdfdsfdsfds");
		String json = JacksonUtils.getJson(first);
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_PUT_COURSE.replace(ControllerAPI.REQUEST_COURSE_ID, course.getSecond().getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_PUT_COURSE.replace(ControllerAPI.REQUEST_COURSE_ID, course.getSecond().getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_PUT_COURSE.replace(ControllerAPI.REQUEST_COURSE_ID, course.getSecond().getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_PUT_COURSE.replace(ControllerAPI.REQUEST_COURSE_ID, course.getSecond().getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Course course1 = JacksonUtils.fromJson(Course.class, contentAsString);
		assertEquals(first.getName(), course1.getName());
		assertEquals(course.getSecond().getId(), course1.getId());
		assertEquals(course.getSecond().getOwner().getId(), course1.getOwner().getId());
	}

	@Test
	void checkCoursesByDivision() throws Exception {
		Pair<CourseBean, Course> course = createCourse();
		CourseBean first = course.getFirst().setName("fsdfdsfdsfds");
		String json = JacksonUtils.getJson(first);
		// unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString())
						.replace(ControllerAPI.REQUEST_PAGE_NUMBER, "0").replace(ControllerAPI.REQUEST_PAGE_SIZE, "10")).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		MockHttpServletResponse response = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString()).replace(ControllerAPI.REQUEST_PAGE_NUMBER, "0")
								.replace(ControllerAPI.REQUEST_PAGE_SIZE, "10"))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
		List<Course> list = JacksonUtils.getListFromJson(Course[].class, response.getContentAsString());

		assertEquals(3, list.size());

		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(1));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(1));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(3));
	}

	@Test
	void checkCoursesByPerson() throws Exception {
		Pair<CourseBean, Course> course = createCourse();
		CourseBean first = course.getFirst().setName("fsdfdsfdsfds");
		String json = JacksonUtils.getJson(first);
		// unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_PERSON_BY_PAGE
				.replace(ControllerAPI.REQUEST_PERSON_ID, person.getId().toString()).replace(ControllerAPI.REQUEST_PAGE_NUMBER, "0").replace(ControllerAPI.REQUEST_PAGE_SIZE, "10")).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_PERSON_BY_PAGE.replace(ControllerAPI.REQUEST_PERSON_ID, person.getId().toString()).replace(ControllerAPI.REQUEST_PAGE_NUMBER, "0")
								.replace(ControllerAPI.REQUEST_PAGE_SIZE, "10"))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Course> list = JacksonUtils.getListFromJson(Course[].class, contentAsString);

		assertEquals(1, list.size());
	}
}