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
import tech.shooting.ipsc.bean.LegendTypeBean;
import tech.shooting.ipsc.bean.VehicleTypeBean;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.LegendType;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.LegendTypeRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;
import tech.shooting.ipsc.service.LegendTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = VehicleTypeRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, LegendTypeController.class, LegendTypeService.class })
class LegendTypeControllerTest {

	private static final String WEAPON_TYPE_ID = "Test";
	
	private static final String LEGEND_TYPE_ID = "Test";

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LegendTypeRepository typeRepository;
	
	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	@Autowired
	private MockMvc mockMvc;

	private User user;

	private User admin;

	private User judge;

	private String adminToken;

	private String judgeToken;

	private String userToken;

	@BeforeEach
	void setUp() {
		typeRepository.deleteAll();
		weaponTypeRepository.deleteAll();
	
		weaponTypeRepository.save(new WeaponType().setName(WEAPON_TYPE_ID));
		
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"))
				.setPerson(new Person().setName("fgdgfgd"))) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void checkGetAllTypes() throws Exception {
		assertEquals(Collections.emptyList(), typeRepository.findAll());
		createFewTypes();
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		List<VehicleType> listFromJson = JacksonUtils.getListFromJson(VehicleType[].class, contentAsString);
		assertEquals(typeRepository.findAll().size(), listFromJson.size());
	}

	private void createFewTypes() {
		List<LegendType> types = new ArrayList<>();
		types.add(new LegendType().setName("AKM"));
		types.add(new LegendType().setName("AK-47"));
		types.add(new LegendType().setName("AK-74"));
		types.add(new LegendType().setName("AKC-74"));
		typeRepository.saveAll(types);
	}

	@Test
	void checkGetTypeById() throws Exception {
		assertEquals(Collections.emptyList(), typeRepository.findAll());
		createFewTypes();
		LegendType type = typeRepository.findAll().get(0);
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, type.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, type.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, type.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.get(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, type.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		LegendType listFromJson = JacksonUtils.fromJson(LegendType.class, contentAsString);
		assertEquals(type, listFromJson);
	}

	@Test
	void checkPostType() throws Exception {
		assertEquals(Collections.emptyList(), typeRepository.findAll());
		LegendTypeBean bean = new LegendTypeBean().setName(LEGEND_TYPE_ID);
		String json = JacksonUtils.getJson(bean);
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_POST_TYPE).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_POST_TYPE).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_POST_TYPE).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_POST_TYPE).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		LegendType typeFromDB = JacksonUtils.fromJson(LegendType.class, contentAsString);
		assertEquals(1, typeRepository.findAll().size());
		assertEquals(bean.getName(), typeFromDB.getName());
	}

	@Test
	void checkDeleteType() throws Exception {
		assertEquals(Collections.emptyList(), typeRepository.findAll());
		LegendType bean = new LegendType().setName(LEGEND_TYPE_ID);
		bean = typeRepository.save(bean);
		// try access with unauthorized user role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_DELETE_TYPE_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, bean.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_DELETE_TYPE_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, bean.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_DELETE_TYPE_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, bean.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		mockMvc.perform(
				MockMvcRequestBuilders.delete(ControllerAPI.LEGEND_TYPE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.LEGEND_TYPE_CONTROLLER_DELETE_TYPE_BY_ID.replace(ControllerAPI.REQUEST_TYPE_ID, bean.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(0, typeRepository.findAll().size());
	}
}