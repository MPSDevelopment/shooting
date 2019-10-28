package tech.shooting.ipsc.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;
import tech.shooting.ipsc.service.WeaponService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = WeaponRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, WeaponController.class, WeaponService.class })
class WeaponControllerTest {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private WeaponRepository weaponRepository;

	@Autowired
	private MockMvc mockMvc;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	private Person testPerson;

	private Division testDivision;

	private WeaponType testWeaponType;

	private Weapon testWeapon;

	private WeaponBean testWeaponBean;

	@BeforeEach
	void setUp() {
		weaponRepository.deleteAll();
		testWeapon = new Weapon().setCount(0).setOwner(testPerson).setWeaponName(testWeaponType).setSerialNumber("1234567");
		testDivision = testDivision == null ? divisionRepository.save(new Division().setParent(null).setName("root")) : testDivision;
		testPerson = testPerson == null ? personRepository.save(new Person().setDivision(testDivision).setName("testing").setQualifierRank(ClassificationBreaks.D)) : testPerson;
		testWeaponType = testWeaponType == null ? weaponTypeRepository.save(new WeaponType().setName("Test-AK")) : testWeaponType;
		testWeaponBean = new WeaponBean().setId(testWeapon.getId()).setWeaponType(testWeaponType.getId()).setCount(0).setOwner(testPerson.getId()).setSerialNumber("1234567");
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"))
				.setPerson(new Person().setName("fgdgfgd"))) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void getAllWeapon() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 10;
		createWeaponRows(count);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Weapon> listFromJson = JacksonUtils.getListFromJson(Weapon[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void getWeaponById() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 10;
		createWeaponRows(count);
		Weapon weapon = weaponRepository.findAll().get(0);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, weapon.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, weapon.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, weapon.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, weapon.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Weapon weaponFromDB = JacksonUtils.fromJson(Weapon.class, contentAsString);
		assertEquals(weapon, weaponFromDB);
	}

	@Test
	void getWeaponByDivision() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 10;
		createWeaponRows(count);
		assertEquals(count, weaponRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Weapon> listFromJson = JacksonUtils.getListFromJson(Weapon[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void checkGetWeaponByPersonNameAndDivisionID() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 10;
		createWeaponRows(count);
		assertEquals(count, weaponRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Weapon> listFromJson = JacksonUtils.getListFromJson(Weapon[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	private void createWeaponRows(int k) {
		for (int i = 0; i < k; i++) {
			weaponRepository.save(new Weapon().setCount(i).setOwner(testPerson).setWeaponName(testWeaponType).setSerialNumber("1234567" + i));
		}
	}

	@Test
	void getWeaponByPerson() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 5;
		createWeaponRows(count);
		assertEquals(count, weaponRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Weapon> listFromJson = JacksonUtils.getListFromJson(Weapon[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void postWeapon() throws Exception {
		assertEquals(Collections.emptyList(), weaponRepository.findAll());
		int count = weaponRepository.findAll().size();
		String json = JacksonUtils.getJson(testWeaponBean);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		
		 mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON).contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		 
		json = JacksonUtils.getJson(testWeaponBean.setSerialNumber(testWeaponBean.getSerialNumber() + "1"));
		
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Weapon weapon = JacksonUtils.fromJson(Weapon.class, contentAsString);
		assertEquals(count + 2, weaponRepository.findAll().size());
		assertEquals(testWeaponBean.getCount(), weapon.getCount());
		assertEquals(testWeaponBean.getOwner(), weapon.getOwner().getId());
		assertEquals(testWeaponBean.getSerialNumber(), weapon.getSerialNumber());
		assertEquals(testWeaponBean.getWeaponType(), weapon.getWeaponName().getId());
	}

	@Test
	void deleteWeapon() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());
		int count = 0;
		Weapon save = weaponRepository.save(testWeapon);
		assertEquals(count + 1, weaponRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(count, weaponRepository.findAll().size());
	}

	@Test
	void postWeaponAddOwner() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());

		Weapon save = weaponRepository.save(testWeapon.setOwner(null));

		assertEquals(1, weaponRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Weapon weapon = JacksonUtils.fromJson(Weapon.class, contentAsString);
		assertEquals(1, weaponRepository.findAll().size());
		assertEquals(testPerson.getId(), weapon.getOwner().getId());
	}

	@Test
	void postWeaponRemoveOwner() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());

		Weapon save = weaponRepository.save(testWeapon.setOwner(testPerson));

		assertEquals(1, weaponRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Weapon weapon = JacksonUtils.fromJson(Weapon.class, contentAsString);
		assertEquals(1, weaponRepository.findAll().size());
		assertEquals(null, weapon.getOwner());
	}

	@Test
	void postWeaponAddShootings() throws Exception {
		assertEquals(0, weaponRepository.findAll().size());

		Weapon save = weaponRepository.save(testWeapon.setCount(0));
		Integer fireCount = 50;

		assertEquals(1, weaponRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Weapon weapon = JacksonUtils.fromJson(Weapon.class, contentAsString);
		assertEquals(1, weaponRepository.findAll().size());
		assertEquals(fireCount, weapon.getCount());
	}

	@Test
	void postWeaponAddShootingsIncorrectFireCount() throws Exception {
		Weapon save = weaponRepository.save(testWeapon.setCount(100));
		Integer fireCount = 50;
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.WEAPON_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT.replace(ControllerAPI.REQUEST_WEAPON_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}