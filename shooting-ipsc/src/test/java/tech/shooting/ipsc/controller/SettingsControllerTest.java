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
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Settings;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.SettingsRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.SettingsService;

import java.util.Collections;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = EquipmentTypeRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, SettingsService.class, DatabaseCreator.class, SettingsController.class})
class SettingsControllerTest {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SettingsRepository settingsRepository;

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
		user = user == null ? userRepository.save(new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"))
				.setPerson(new Person().setName("fgdgfgd"))) : user;
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	void checkGetSetting() throws Exception {
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_GET_SETTINGS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_GET_SETTINGS).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_GET_SETTINGS).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_GET_SETTINGS).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var settings = JacksonUtils.fromJson(Settings.class, contentAsString);
		
		assertEquals(DatabaseCreator.DEFAULT_SETTINGS_NAME, settings.getName());
	}

	@Test
	void checkPutSetting() throws Exception {
		var settings = new Settings().setTagServiceIp("129.0.1.2");
		String json = JacksonUtils.getJson(settings);
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_PUT_SETTINGS).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_PUT_SETTINGS).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_PUT_SETTINGS).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.SETTINGS_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_PUT_SETTINGS).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var result = JacksonUtils.fromJson(Settings.class, contentAsString);
		
		assertEquals(DatabaseCreator.DEFAULT_SETTINGS_NAME, result.getName());
		assertEquals(settings.getTagServiceIp(), result.getTagServiceIp());
	}
}