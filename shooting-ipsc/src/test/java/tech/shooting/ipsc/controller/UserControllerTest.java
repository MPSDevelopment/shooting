package tech.shooting.ipsc.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.Token.TokenType;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.config.CorsConfig;
import tech.shooting.ipsc.config.IpscConstants;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.UserService;
import tech.shooting.ipsc.utils.UserLockUtils;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@ContextConfiguration(classes = { IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, CorsConfig.class, DatabaseCreator.class, TokenAuthenticationManager.class, TokenAuthenticationFilter.class,
		IpscUserDetailsService.class, UserController.class, UserService.class, UserDao.class, UserLockUtils.class, ValidationErrorHandler.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class UserControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private IpscSettings settings;

	private String body;

	private User user;
	private User admin;

	private Map<String, ValidationException> errors;

	private String token;

	private String userJson;

	@BeforeEach
	public void before() {
		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER);
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		userJson = JacksonUtils.getFullJson(user);
	}

	@Test
	public void checkSignUp() throws Exception {

		// try to access status with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to create user with non admin user
		token = tokenUtils.createToken(admin.getId(), TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, token))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to create empty user with admin user
		token = tokenUtils.createToken(admin.getId(), TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, token))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		long count = userRepository.count();
		
		// try to create user with admin user
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, token).contentType(MediaType.APPLICATION_JSON).content(userJson))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		assertEquals(count + 1, userRepository.count());
		
		// try to create the same user
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE).header(Token.TOKEN_HEADER, token).contentType(MediaType.APPLICATION_JSON).content(userJson))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

}