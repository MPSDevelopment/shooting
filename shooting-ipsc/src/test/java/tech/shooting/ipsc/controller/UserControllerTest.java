package tech.shooting.ipsc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.Token.TokenType;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.UserService;
import tech.shooting.ipsc.utils.UserLockUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, DatabaseCreator.class, TokenAuthenticationManager.class, TokenAuthenticationFilter.class,
	IpscUserDetailsService.class, UserController.class, UserService.class, UserDao.class, UserLockUtils.class, ValidationErrorHandler.class})
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
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IpscSettings settings;

	private String body;

	private User user;
	private User admin;

	private Map<String, ValidationException> errors;

	private String adminToken;

	private String userJson;

	private String userToken;

	private String url;

	@BeforeEach
	public void before () {
		userRepository.deleteAll();

		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.save(new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD).setRoleName(RoleName.ADMIN).setActive(true).setName("Admin"));
		userJson = JacksonUtils.getJson(user);

		userToken = adminToken = tokenUtils.createToken(admin.getId(), TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

	}

	@Test
	public void checkSignupJudge () throws Exception {
		// try to create empty user with admin user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_USER).header(Token.TOKEN_HEADER, adminToken))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		long count = userRepository.count();
		userJson = JacksonUtils.getJson(new UserSignupBean().setPassword("fsdfkjdhsfjdhskjfs").setName("fdfdfdfd").setLogin("fdfdfdfdfdfd"));
		// try to create user with admin user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_USER)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(userJson)).andExpect(MockMvcResultMatchers.status().isCreated());
		assertEquals(count + 1, userRepository.count());

		// try to create the same user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_USER)
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(userJson)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void checkUpdate () throws Exception {

		user = userRepository.save(user);
		userJson = JacksonUtils.getJson(user.setName("testds"));
		// try to access update with admin user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_PUT_USER.replace("{userId}", user.getId().toString()))
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(userJson)).andExpect(MockMvcResultMatchers.status().isOk());

		user = userRepository.findByLogin(user.getLogin());
		assertEquals("testds", user.getName());

	}

	@Test
	public void checkUpdatePassword () throws Exception {
		User testUser = userRepository.save(user);
		ChangePasswordBean changePasswordBean = new ChangePasswordBean();
		changePasswordBean.setId(testUser.getId());
		changePasswordBean.setNewPassword("54321");
		// try to access update password with admin user
		userJson = JacksonUtils.getFullJson(changePasswordBean);

		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_CHANGE_PASSWORD.replace("{userId}", testUser.getId().toString()))
			.header(Token.TOKEN_HEADER, adminToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(userJson)).andExpect(MockMvcResultMatchers.status().isOk());

		assertTrue(passwordEncoder.matches("54321", userRepository.findByLogin(testUser.getLogin()).getPassword()));

	}

	@Test
	public void checkGetUser () throws Exception {

		// try to access get user method unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER.replace("{userId}", String.valueOf(455645646))))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access get user method non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER.replace("{userId}", String.valueOf(455645646))).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access get user method admin user
		User testUser = userRepository.save(user);
		String contentAsString = mockMvc.perform(
			MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER.replace("{userId}", String.valueOf(testUser.getId()))).header(Token.TOKEN_HEADER, adminToken))
			                         .andExpect(MockMvcResultMatchers.status().isOk())
			                         .andReturn()
			                         .getResponse()
			                         .getContentAsString();
		User user = JacksonUtils.fromJson(User.class, contentAsString);
		assertEquals(user.getLogin(), testUser.getLogin());
		assertEquals(user.getName(), testUser.getName());
		assertEquals(user.getId(), testUser.getId());
	}

	@Test
	public void checkGetAllUsers () throws Exception {

		// try to access get all users method unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access get all users method non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access get all users method admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS).header(Token.TOKEN_HEADER, adminToken))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> res = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Object>>() {
		});
		assertEquals(userRepository.findAll().size(), res.size());
	}

	@Test
	public void checkDelete () throws Exception {

		// try to access delete user with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER.replace("{userId}", "1")))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to delete not existing user with admin user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER.replace("{userId}", RandomStringUtils.randomNumeric(6)))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		user = userRepository.save(user);

		// try to delete user with admin user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER.replace("{userId}", user.getId().toString()))
			.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertFalse(userRepository.existsById(user.getId()));

	}

	@Test
	public void checkGetCount () throws Exception {

		// try to access getCount with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access getCount with non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access getCount with admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
			.andExpect(MockMvcResultMatchers.status().isOk());

		// compare getCount() & userRepository.count
		long count = userRepository.count();

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT).header(Token.TOKEN_HEADER, adminToken))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		assertEquals(mvcResult.getResponse().getContentAsString(), String.valueOf(count));

	}

	@Test
	public void checkGetAllUsersByPage () throws Exception {

		createUsers(40);

		// try to access getAllUsersByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try to access getAllUsersByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try to access getAllUsersByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize}", String.valueOf(5))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		List<User> list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());

		// try to access getAllUsersByPage with admin user with size 30
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(1))
			.replace("{pageSize" + "}", String.valueOf(30))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		list = JacksonUtils.getListFromJson(User[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(20, list.size());

	}

	@Test
	public void checkGetAllUsersByPagePart2 () throws Exception {
		// try to access to header
		int sizeAllUser = userRepository.findAll().size();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE.replace("{pageNumber}", String.valueOf(page))
			.replace("{pageSize}", String.valueOf(size))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(response.getHeader("pages"), String.valueOf(countPages));
		assertEquals(response.getHeader("page"), String.valueOf(page));
		assertEquals(response.getHeader("total"), String.valueOf(sizeAllUser));

	}

	private void createUsers (int count) {
		for(int i = 0; i < count; i++) {
			var user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(RandomStringUtils.randomAlphanumeric(15)).setRoleName(RoleName.USER);
			userRepository.save(user);
			log.info("User %s has been created", user.getLogin());
		}
	}

	@Test
	public void checkGetJudges () throws Exception {

		//try access to getJudges with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_JUDGES)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		//try access to getJudges with non admin user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_JUDGES).header(Token.TOKEN_HEADER, userToken))
			.andExpect(MockMvcResultMatchers.status().isForbidden());

		//try access to getJudges with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.USER_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_JUDGES).header(Token.TOKEN_HEADER, adminToken))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		assertEquals(userRepository.findByRoleName(RoleName.JUDGE).size(), JacksonUtils.getListFromJson(User[].class, contentAsString).size());
	}

}