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
import tech.shooting.ipsc.bean.SpecialityBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Speciality;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.SpecialityRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.service.SpecialityService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = SpecialityRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, SpecialityController.class, SpecialityService.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class SpecialityControllerTest {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

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

	private Speciality specialityTest;

	@BeforeEach
	public void before() {
		specialityRepository.deleteAll();
		specialityTest = specialityRepository.save(new Speciality().setSpecialityKz("specialist").setSpecialityRus("даун в 3 поколении"));
		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void checkGetAllSpeciality() throws Exception {
		addSpecialityToDb(5);
		// try access to get all speciality with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to get all speciality with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to get all speciality with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to get all speciality with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		List<Speciality> ts = JacksonUtils.getListFromJson(Speciality[].class, contentAsString);
		assertEquals(specialityRepository.findAll().size(), ts.size());
	}

	@Test
	void checkGetSpecialityById() throws Exception {
		addSpecialityToDb(5);
		Speciality speciality = specialityRepository.findAll().get(0);
		// try access to by id speciality with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to by id speciality with user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to by id speciality with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to get by id speciality with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		Speciality fromDB = JacksonUtils.fromJson(Speciality.class, contentAsString);
		assertEquals(speciality, fromDB);
	}

	@Test

	void checkGetSpecialityByIdIncorrectData() throws Exception {
		// try access to get by id speciality with admin role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, String.valueOf(4256342146521436521L)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	void checkPostSpeciality() throws Exception {
		SpecialityBean bean = new SpecialityBean().setSpecialityKz("Specialist").setSpecialityRus("Алкаш");
		String json = JacksonUtils.getJson(bean);

		// try access to create speciality with admin role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_POST_SPECIALITY).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to create speciality with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_POST_SPECIALITY).header(Token.TOKEN_HEADER, userToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to create speciality with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_POST_SPECIALITY).header(Token.TOKEN_HEADER, judgeToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to create speciality with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_POST_SPECIALITY).header(Token.TOKEN_HEADER, adminToken)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		Speciality fromDb = JacksonUtils.fromJson(Speciality.class, contentAsString);
		checkBeanWithSpeciality(bean, fromDb);

	}

	@Test
	void checkPutSpeciality() throws Exception {
		SpecialityBean specialityBean = new SpecialityBean().setSpecialityKz("SPECIALIST").setSpecialityRus("ДАУН БЕЗ ВАРИАНТОВ");
		String json = JacksonUtils.getJson(specialityBean);
		// try access to put speciality with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.put(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY.replace(ControllerAPI.REQUEST_SPECIALITY_ID, specialityTest.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to put speciality with user role
		mockMvc.perform(
				MockMvcRequestBuilders.put(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY.replace(ControllerAPI.REQUEST_SPECIALITY_ID, specialityTest.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to put speciality with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.put(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY.replace(ControllerAPI.REQUEST_SPECIALITY_ID, specialityTest.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to put speciality with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.put(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY.replace(ControllerAPI.REQUEST_SPECIALITY_ID, specialityTest.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Speciality fromDb = JacksonUtils.fromJson(Speciality.class, contentAsString);
		checkBeanWithSpeciality(specialityBean, fromDb);
		assertEquals(specialityTest.getId(), fromDb.getId());
	}

	@Test
	void checkPutSpecialityWithIncorrectId() throws Exception {
		SpecialityBean specialityBean = new SpecialityBean().setSpecialityKz("SPECIALIST").setSpecialityRus("ДАУН БЕЗ ВАРИАНТОВ");
		String json = JacksonUtils.getJson(specialityBean);

		// try access to put speciality with admin role
		mockMvc.perform(
				MockMvcRequestBuilders.put(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY.replace(ControllerAPI.REQUEST_SPECIALITY_ID, String.valueOf(213232323L)))
						.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	private void checkBeanWithSpeciality(SpecialityBean bean, Speciality fromDb) {
		assertEquals(bean.getSpecialityKz(), fromDb.getSpecialityKz());
		assertEquals(bean.getSpecialityRus(), fromDb.getSpecialityRus());
		assertNotNull(fromDb.getId());
	}

	private void addSpecialityToDb(int i) {
		int count = specialityRepository.findAll().size();
		for (int j = 0; j < i; j++) {
			specialityRepository.save(new Speciality().setSpecialityRus("Алкаш в поколении " + i).setSpecialityKz("Medic " + i));
		}
		assertEquals(specialityRepository.findAll().size(), count + i);
	}

	@Test
	void checkDeleteSpecialityById() throws Exception {
		addSpecialityToDb(5);
		Speciality speciality = specialityRepository.findAll().get(0);
		// try access to delete speciality with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access to delete speciality with user role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to delete speciality with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access to delete speciality with admin role
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, speciality.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(Optional.empty(), specialityRepository.findById(speciality.getId()));
	}

	@Test
	void checkDeleteSpecialityByIdWithIncorrectId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID.replace(ControllerAPI.REQUEST_SPECIALITY_ID, String.valueOf(34253452L)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
}