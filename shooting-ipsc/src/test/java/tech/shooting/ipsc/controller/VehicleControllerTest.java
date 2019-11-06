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
import tech.shooting.ipsc.bean.VehicleBean;
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
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.repository.VehicleRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.service.VehicleService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = WeaponRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, VehicleController.class, VehicleService.class })
class VehicleControllerTest {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VehicleTypeRepository vehicleTypeRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

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

	private VehicleType testType;

	private Vehicle testVehicle;

	private VehicleBean testVehicleBean;

	@BeforeEach
	void setUp() {
		vehicleRepository.deleteAll();
		testVehicle = new Vehicle().setCount(0).setOwner(testPerson).setType(testType).setSerialNumber("1234567");
		testDivision = testDivision == null ? divisionRepository.save(new Division().setParent(null).setName("root")) : testDivision;
		testPerson = testPerson == null ? personRepository.save(new Person().setDivision(testDivision).setName("testing").setQualifierRank(ClassificationBreaks.D)) : testPerson;
		testType = testType == null ? vehicleTypeRepository.save(new VehicleType().setName("Test-AK")) : testType;
		testVehicleBean = new VehicleBean().setId(testVehicle.getId()).setType(testType.getId()).setCount(0).setOwner(testPerson.getId()).setSerialNumber("1234567");
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"))
				.setPerson(new Person().setName("fgdgfgd"))) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void getAllVehicle() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 10;
		createRows(count);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Vehicle> listFromJson = JacksonUtils.getListFromJson(Vehicle[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void getVehicleById() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 10;
		createRows(count);
		Vehicle vehicle = vehicleRepository.findAll().get(0);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, vehicle.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, vehicle.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, vehicle.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, vehicle.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Vehicle vehicleFromDB = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(vehicle, vehicleFromDB);
	}

	@Test
	void getVehicleByDivision() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 10;
		createRows(count);
		assertEquals(count, vehicleRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Vehicle> listFromJson = JacksonUtils.getListFromJson(Vehicle[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void checkGetVehicleByPersonNameAndDivisionID() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 10;
		createRows(count);
		assertEquals(count, vehicleRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, testDivision.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_NAME, testPerson.getName()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Vehicle> listFromJson = JacksonUtils.getListFromJson(Vehicle[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	private void createRows(int k) {
		for (int i = 0; i < k; i++) {
			vehicleRepository.save(new Vehicle().setCount(i).setOwner(testPerson).setType(testType).setSerialNumber("1234567" + i));
		}
	}

	@Test
	void getVehicleByPerson() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 5;
		createRows(count);
		assertEquals(count, vehicleRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID.replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<Vehicle> listFromJson = JacksonUtils.getListFromJson(Vehicle[].class, contentAsString);
		assertEquals(count, listFromJson.size());
	}

	@Test
	void postVehicle() throws Exception {
		assertEquals(Collections.emptyList(), vehicleRepository.findAll());
		int count = vehicleRepository.findAll().size();
		String json = JacksonUtils.getJson(testVehicleBean);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		json = JacksonUtils.getJson(testVehicleBean.setSerialNumber(testVehicleBean.getSerialNumber() + "1"));
		
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		Vehicle vehicle = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(count + 2, vehicleRepository.findAll().size());
		assertEquals(testVehicleBean.getCount(), vehicle.getCount());
		assertEquals(testVehicleBean.getOwner(), vehicle.getOwner().getId());
		assertEquals(testVehicleBean.getSerialNumber(), vehicle.getSerialNumber());
		assertEquals(testVehicleBean.getType(), vehicle.getType().getId());
	}
	
	@Test
	void putVehicle() throws Exception {
		assertEquals(Collections.emptyList(), vehicleRepository.findAll());
		int count = vehicleRepository.findAll().size();
		String json = JacksonUtils.getJson(testVehicleBean);
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_PUT).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_PUT).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_PUT).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_PUT).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		json = JacksonUtils.getJson(testVehicleBean.setSerialNumber(testVehicleBean.getSerialNumber() + "1"));
		
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_PUT).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		Vehicle vehicle = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(count + 2, vehicleRepository.findAll().size());
		assertEquals(testVehicleBean.getCount(), vehicle.getCount());
		assertEquals(testVehicleBean.getOwner(), vehicle.getOwner().getId());
		assertEquals(testVehicleBean.getSerialNumber(), vehicle.getSerialNumber());
		assertEquals(testVehicleBean.getType(), vehicle.getType().getId());
	}

	@Test
	void deleteWeapon() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());
		int count = 0;
		Vehicle save = vehicleRepository.save(testVehicle);
		assertEquals(count + 1, vehicleRepository.findAll().size());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(count, vehicleRepository.findAll().size());
	}

	@Test
	void postWeaponAddOwner() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());

		Vehicle save = vehicleRepository.save(testVehicle.setOwner(null));

		assertEquals(1, vehicleRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_PERSON_ID, testPerson.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Vehicle weapon = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(1, vehicleRepository.findAll().size());
		assertEquals(testPerson.getId(), weapon.getOwner().getId());
	}

	@Test
	void postWeaponRemoveOwner() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());

		Vehicle save = vehicleRepository.save(testVehicle.setOwner(testPerson));

		assertEquals(1, vehicleRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_REMOVE_OWNER.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Vehicle weapon = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(1, vehicleRepository.findAll().size());
		assertEquals(null, weapon.getOwner());
	}

	@Test
	void postWeaponAddShootings() throws Exception {
		assertEquals(0, vehicleRepository.findAll().size());

		Vehicle save = vehicleRepository.save(testVehicle.setCount(0));
		Integer fireCount = 50;

		assertEquals(1, vehicleRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Vehicle weapon = JacksonUtils.fromJson(Vehicle.class, contentAsString);
		assertEquals(1, vehicleRepository.findAll().size());
		assertEquals(fireCount, weapon.getCount());
	}

	@Test
	void postWeaponAddShootingsIncorrectFireCount() throws Exception {
		Vehicle save = vehicleRepository.save(testVehicle.setCount(100));
		Integer fireCount = 50;
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.VEHICLE_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT.replace(ControllerAPI.REQUEST_VEHICLE_ID, save.getId().toString()).replace(ControllerAPI.REQUEST_FIRED_COUNT, fireCount.toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}