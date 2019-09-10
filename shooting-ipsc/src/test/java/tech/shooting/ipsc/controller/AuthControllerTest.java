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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.service.UserService;
import tech.shooting.ipsc.utils.UserLockUtils;

import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@ContextConfiguration(classes = { DatabaseCreator.class, UserDao.class, IpscMongoConfig.class, SecurityConfig.class, AuthController.class, UserService.class, UserLockUtils.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class AuthControllerTest {
	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User user;

	private User admin;

	private User judge;

	private String userJson;

	private User userFromDB;

	private String token;

	private String tokenUser;

	private String tokenJudge;

	private String tokenAdmin;

	@Autowired
	private UserDao userDao;

	private User guest;

	private String tokenGuest;

	@BeforeEach
	public void before() {
		userRepository.deleteByRoleName(RoleName.USER);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15).toLowerCase()).setPassword("123456").setActive(true);
		userJson = JacksonUtils.getFullJson(user);
		userFromDB = userRepository.save(user.setPassword(passwordEncoder.encode(user.getPassword())).setRoleName(RoleName.USER).setName("testing name"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		guest = userRepository.findByLogin(DatabaseCreator.GUEST_LOGIN);
		tokenUser = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		tokenAdmin = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		tokenJudge = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		tokenGuest = tokenUtils.createToken(guest.getId(), Token.TokenType.USER, guest.getLogin(), RoleName.GUEST, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	public void checkPostStatus() throws Exception {
		// try to access status with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access status with authorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS).header(Token.TOKEN_HEADER, tokenUser))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access status with judge user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS).header(Token.TOKEN_HEADER, tokenUser))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
		// try to access status with admin user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS).header(Token.TOKEN_HEADER, tokenAdmin))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void checkPostLogin() throws Exception {
		// try to login with an empty user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to login with not a registered user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(JacksonUtils.getJson(new User().setActive(true).setLogin(RandomStringUtils.randomAlphanumeric(15).toLowerCase()).setPassword("hfjdghfjd")))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to login with a registered user
		token = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).contentType(MediaType.APPLICATION_JSON_UTF8).content(userJson))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getHeader(Token.TOKEN_HEADER);
		assertNotNull(token);
		// try to login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).header("Origin", "http://www.someurl.com").contentType(MediaType.APPLICATION_JSON)
				.content(userJson)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getHeader(Token.TOKEN_HEADER);
		// try to login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, token)).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void checkPostLoginNotExisted() throws Exception {
		
	}
	
	@Test
	public void checkPostLoginGuest() throws Exception {
		userJson = JacksonUtils.getFullJson(new User().setLogin(DatabaseCreator.GUEST_LOGIN).setPassword(DatabaseCreator.GUEST_PASSWORD));
		// try to login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).header("Origin", "http://www.someurl.com").contentType(MediaType.APPLICATION_JSON)
				.content(userJson)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getHeader(Token.TOKEN_HEADER);
		// try to login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, tokenGuest)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void checkPostLogOut() throws Exception {
		// try to logout with a bad header
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, token + "test"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to logout with a user header
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, tokenUser))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}