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
import tech.shooting.ipsc.bean.EquipmentBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Equipment;
import tech.shooting.ipsc.pojo.EquipmentType;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.EquipmentRepository;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.service.EquipmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = WeaponRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, EquipmentController.class, EquipmentService.class })
class EquipmentControllerTest {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EquipmentTypeRepository typeRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private EquipmentRepository repository;

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

	private EquipmentType testType;

	private Equipment testEquipment;

	private EquipmentBean testEquipmentBean;

	@BeforeEach
	void setUp() {
		repository.deleteAll();
		testEquipment = new Equipment().setOwner(testPerson).setType(testType).setSerialNumber("1234567");
		testDivision = testDivision == null ? divisionRepository.save(new Division().setParent(null).setName("root")) : testDivision;
		testPerson = testPerson == null ? personRepository.save(new Person().setDivision(testDivision).setName("testing").setQualifierRank(ClassificationBreaks.D)) : testPerson;
		testType = testType == null ? typeRepository.save(new EquipmentType().setName("Test-AK")) : testType;
		testEquipmentBean = new EquipmentBean().setType(testType.getId()).setOwner(testPerson.getId()).setSerialNumber("1234567");
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"))
				.setPerson(new Person().setName("fgdgfgd"))) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void getAll() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 10;
		createRows(count);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Equipment> listFromJson = JacksonUtils.getListFromJson(Equipment[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void getById() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 10;
		createRows(count);
		Equipment vehicle = repository.findAll().get(0);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, vehicle.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, vehicle.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, vehicle.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, vehicle.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Equipment weaponFromDB = JacksonUtils.fromJson(Equipment.class, contentAsString);
		assertEquals(vehicle, weaponFromDB);
	}

	@Test
	void getByDivision() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 10;
		createRows(count);
		assertEquals(count, repository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Equipment> listFromJson = JacksonUtils.getListFromJson(Equipment[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void checkGetByPersonNameAndDivisionID() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 10;
		createRows(count);
		assertEquals(count, repository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Equipment> listFromJson = JacksonUtils.getListFromJson(Equipment[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	private void createRows(int k) {
		for (int i = 0; i < k; i++) {
			repository.save(new Equipment().setOwner(testPerson).setType(testType).setSerialNumber("1234567" + i));
		}
	}

	@Test
	void getByPerson() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 5;
		createRows(count);
		assertEquals(count, repository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Equipment> listFromJson = JacksonUtils.getListFromJson(Equipment[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void post() throws Exception {
		assertEquals(Collections.emptyList(), repository.findAll());
		int count = repository.findAll().size();
		String json = JacksonUtils.getJson(testEquipmentBean);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Equipment weapon = JacksonUtils.fromJson(Equipment.class, contentAsString);
		assertEquals(count + 1, repository.findAll().size());
//		assertEquals(testEquipmentBean.getCount(), weapon.getCount());
		assertEquals(testEquipmentBean.getOwner(), weapon.getOwner().getId());
		assertEquals(testEquipmentBean.getSerialNumber(), weapon.getSerialNumber());
		assertEquals(testEquipmentBean.getType(), weapon.getType().getId());
	}

	@Test
	void delete() throws Exception {
		assertEquals(0, repository.findAll().size());
		int count = 0;
		Equipment save = repository.save(testEquipment);
		assertEquals(count + 1, repository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(count, repository.findAll().size());
	}

	@Test
	void postAddOwner() throws Exception {
		assertEquals(0, repository.findAll().size());

		Equipment save = repository.save(testEquipment.setOwner(null));

		assertEquals(1, repository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.EQUIPMENT_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.EQUIPMENT_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Equipment weapon = JacksonUtils.fromJson(Equipment.class, contentAsString);
		assertEquals(1, repository.findAll().size());
		assertEquals(testPerson.getId(), weapon.getOwner().getId());
	}

	@Test
	void postRemoveOwner() throws Exception {
		assertEquals(0, repository.findAll().size());

		Equipment save = repository.save(testEquipment.setOwner(testPerson));

		assertEquals(1, repository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post(ControllerAPI.EQUIPMENT_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_EQUIPMENT_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Equipment weapon = JacksonUtils.fromJson(Equipment.class, contentAsString);
		assertEquals(1, repository.findAll().size());
		assertEquals(null, weapon.getOwner());
	}

}