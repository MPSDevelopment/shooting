package tech.shooting.ipsc.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import tech.shooting.ipsc.config.IpscMongoConfig;
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
@ContextConfiguration(classes = {DatabaseCreator.class, UserDao.class, IpscMongoConfig.class, TokenUtils.class, IpscUserDetailsService.class, TokenAuthenticationManager.class, TokenAuthenticationFilter.class,
	SecurityConfig.class, AuthController.class, UserService.class, UserLockUtils.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User user;

	private String userJson;

	private User userFromDB;

	@BeforeEach
	public void before () {
		userRepository.deleteAll();
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15).toLowerCase()).setPassword("123456").setActive(true);
		userJson = JacksonUtils.getFullJson(user);
	}

	@Test
	public void checkPostStatus () throws Exception {
		// try to access status with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	public void checkPostLogin () throws Exception {
		// try to login with an empty user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		// try to login with not a registered user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).contentType(MediaType.APPLICATION_JSON).content(userJson))
		       .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	@Test
	public void checkLogin () throws Exception {
		user = user.setPassword(passwordEncoder.encode(user.getPassword())).setRoleName(RoleName.USER);
		System.out.println("*************************************************************");
		log.info("User to DB %s", user);
		userRepository.save(user);
		// login to the system
		String token =
			mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).contentType(MediaType.APPLICATION_JSON).content(userJson))
			       .andExpect(MockMvcResultMatchers.status().isOk())
			       .andReturn()
			       .getResponse()
			       .getHeader(Token.TOKEN_HEADER);

		/*
		// login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN)
		                                      .header("Origin", "http://www.someurl.com")
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(userJson)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getHeader(Token.TOKEN_HEADER);
		// login to the system from other server, check cors
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, token))
		       .andExpect(MockMvcResultMatchers.status().isOk());
		// try to logout with a bad header
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT).header(Token.TOKEN_HEADER, token + "test"))
		       .andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access status with authorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_STATUS).header(Token.TOKEN_HEADER, token))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
		// login with admin user
		UserLogin userLogin = new UserLogin().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
		token = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN)
		                                              .contentType(MediaType.APPLICATION_JSON)
		                                              .content(JacksonUtils.getFullJson(userLogin))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getHeader(Token.TOKEN_HEADER);
	*/
	}
}