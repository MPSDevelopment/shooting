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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.bean.DivisionDropList;
import tech.shooting.ipsc.bean.UpdateDivisionBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.DivisionService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = DivisionRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, DivisionController.class, DivisionService.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class DivisionControllerTest {
	@Autowired
	private DivisionService divisionService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DivisionRepository divisionRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User admin;

	private String adminToken;

	private String userToken;

	private DivisionBean divisionBean;

	private Division root;

	@BeforeEach
	public void before() {
		divisionService.deleteAllDivision();
		
		root = divisionRepository.createIfNotExists(new Division().setName("??????").setActive(true));
		
		divisionBean = new DivisionBean().setParent(getRootDivision()).setName("root");
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("8523").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	private Long getRootDivision() {
		root = divisionRepository.findByParentIsNull().orElse(null);
		
		log.info("Root is %s", root);
		
		return root == null ? null : root.getId();
	}

	@Test
	public void checkCreateDivision() throws Exception {
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(Objects.requireNonNull(JacksonUtils.getJson(divisionBean)))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(Objects.requireNonNull(JacksonUtils.getJson(divisionBean))).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(Objects.requireNonNull(JacksonUtils.getJson(divisionBean))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		
		DivisionBean division = JacksonUtils.fromJson(DivisionBean.class, contentAsString);
		assertEquals(division.getName(), divisionBean.getName());
		assertEquals(division.getParent(), divisionBean.getParent());
		
		root = divisionRepository.findByParentIsNull().orElse(null);
		
		assertEquals(1, root.getChildren().size());
	}

	@Test
	public void checkAddedChildToTheRoot() throws Exception {
		assertEquals(1, divisionService.getCount());
		DivisionBean division = divisionService.createDivision(divisionBean, getRootDivision());
		assertEquals(2, divisionService.getCount());
		divisionBean = new DivisionBean().setParent(division.getId()).setName("first child").setActive(true);
		String json = JacksonUtils.getJson(divisionBean);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(Objects.requireNonNull(json)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		DivisionBean divisionBeanFromBack = JacksonUtils.fromJson(DivisionBean.class, contentAsString);
		assertEquals(division.getId(), divisionBeanFromBack.getParent());
		assertEquals(divisionBean.getName(), divisionBeanFromBack.getName());
		assertEquals(3, divisionService.getCount());
	}

	@Test
	public void checkRemoveDivision() throws Exception {
		assertEquals(1, divisionService.getCount());
		DivisionBean test1 = divisionService.createDivision(new DivisionBean().setName("test1").setParent(getRootDivision()).setActive(true), null);
		assertEquals(2, divisionService.getCount());
		// try access to remove division unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_DELETE_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, test1.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to remove division non admin user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_DELETE_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, test1.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access to remove division admin user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_DELETE_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, test1.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(1, divisionService.getCount());
	}

	@Test
	public void checkGetAllDivision() throws Exception {
		assertEquals(1, divisionService.getCount());
		createDivisions(20);
		assertEquals(21, divisionService.getCount());
		// try access to getAllDivision with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try access to getAllDivision with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
		// try access to getAllDivision with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		DivisionBean[] divisions = JacksonUtils.fromJson(DivisionBean[].class, contentAsString);
		assertEquals(21, divisions.length);
	}

	@Test
	public void checkGetAllDivisionsByPage() throws Exception {
		createDivisions(40);
		// try to access getAllDivisionsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAllDivisionsByPage with authorized user
		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try to access getAllDivisionsByPage with admin user
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		List<DivisionBean> list = JacksonUtils.getListFromJson(DivisionBean[].class, mvcResult.getResponse().getContentAsString());
		assertEquals(10, list.size());
		// try to access getAllDivisionsByPage with admin user with size 30
		mvcResult = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0
										+ ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace("{pageSize" + "}", String.valueOf(30)))
								.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(DivisionBean[].class, contentAsString);
		assertEquals(20, list.size());
	}

	@Test
	public void checkGetAllDivisionsByPagePart2() throws Exception {
		// try to access to header
		int sizeAllUser = divisionService.getCount();
		int page = 250;
		int size = 0;
		int countInAPage = size <= 10 ? 10 : 20;
		int countPages = sizeAllUser % countInAPage == 0 ? sizeAllUser / countInAPage : (sizeAllUser / countInAPage) + 1;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(page)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(size)))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(countPages));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(page));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(sizeAllUser));
	}

	@Test
	public void checkFindOneDivisionById() throws Exception {
		assertEquals(1, divisionService.getCount());
		DivisionBean division = divisionService.createDivision(divisionBean, divisionBean.getParent());
		assertEquals(2, divisionService.getCount());
		// try access to getDivisionById() with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// user 
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
		// try access to getDivisionById() with admin user
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_ID.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(division, JacksonUtils.fromJson(DivisionBean.class, contentAsString));
	}

	@Test
	public void checkGetRoot() throws Exception {
		DivisionBean division = divisionService.createDivision(divisionBean, getRootDivision());
		Division root = divisionRepository.findByParentIsNull().orElse(null);
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_ROOT).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		DivisionBean divisionBean = JacksonUtils.fromJson(DivisionBean.class, contentAsString);
		assertEquals(root.getId(), divisionBean.getId());
		assertEquals(root.getName(), divisionBean.getName());
		assertEquals(root.getParent(), divisionBean.getParent());
		assertEquals(root.getChildren().size(), divisionBean.getChildren().size());
		assertEquals(root.isActive(), divisionBean.isActive());
	}

	@Test
	public void checkUpdateDivision() throws Exception {
		assertEquals(1, divisionService.getCount());
		DivisionBean division = divisionService.createDivision(divisionBean, divisionBean.getParent());
		assertEquals(2, divisionService.getCount());
		division.setName("updateeee");
		// try access to getDivisionById() with admin user
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.put(ControllerAPI.DIVISION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_PUT_DIVISION.replace(ControllerAPI.REQUEST_DIVISION_ID, division.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(Objects.requireNonNull(JacksonUtils.getJson(new UpdateDivisionBean().setId(division.getId()).setName(division.getName())))).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(division, JacksonUtils.fromJson(DivisionBean.class, contentAsString));
	}

	private void createDivisions(int count) { 
		for (int i = 0; i < count; i++) {
			DivisionBean division = new DivisionBean().setActive(true).setName("test + " + i).setParent(getRootDivision());
			divisionService.createDivision(division, division.getParent());
			log.info("Division %s has been created", division.getName());
		}
	}

	@Test
	public void checkUpdateCustom() throws Exception {
		DivisionBean division = divisionService.createDivision(divisionBean, null);
		divisionService.createDivision(new DivisionBean().setName("fdfdfd").setParent(division.getId()), division.getId());
		DivisionBean qyqy = divisionService.updateDivision(division.getId(), "qyqy");
		assertEquals(1, qyqy.getChildren().size());
	}

	@Test
	void check() {
		createDivisions(100);
		List<DivisionDropList> id = mongoTemplate.aggregate(newAggregation(new MatchOperation(Criteria.where("id").exists(true))), Division.class, DivisionDropList.class).getMappedResults();
		for (int i = 0; i < id.size(); i++) {
			log.info("Id is %s, name is %s", id.get(i).getId(), id.get(i).getName());
		}
	}
}