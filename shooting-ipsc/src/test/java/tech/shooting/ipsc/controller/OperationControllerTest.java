package tech.shooting.ipsc.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.OperationBean;
import tech.shooting.ipsc.bean.OperationCombatElementBean;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.bean.OperationCommandantServiceBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.pojo.Info;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.OperationRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;
import tech.shooting.ipsc.service.OperationService;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = OperationRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, OperationController.class, OperationService.class })
public class OperationControllerTest extends BaseControllerTest {

	@Autowired
	private OperationRepository operationRepository;

	@Autowired
	private OperationService operationService;

	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	@Autowired
	private PersonRepository personRepository;

	private Operation testOperation;

	private Person testing;

	@BeforeEach
	public void before() {
		super.before();

		operationRepository.deleteAll();

		testOperation = new Operation().setInfo(new Info().setNamedRus("Test"));
		
		testing = personRepository.save(new Person().setName("testing").setQualifierRank(ClassificationBreaks.D));
		
	}

	@Test
	void getAllOperations() throws Exception {
		assertEquals(Collections.emptyList(), operationRepository.findAll());
		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		List<Standard> listFromJson = JacksonUtils.getListFromJson(Standard[].class, contentAsString);
		assertEquals(Collections.EMPTY_LIST, listFromJson);

		operationRepository.save(testOperation);

		contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		listFromJson = JacksonUtils.getListFromJson(Standard[].class, contentAsString);
		assertEquals(1, listFromJson.size());

	}

	@Test
	public void checkGetAllOperationsByPage() throws Exception {
		createOperations(42);
		// try to access getAllPersonsByPage with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0
				+ ControllerAPI.OPERATION_CONTROLLER_GET_OPERATIONS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(5))))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try to access getAllPersonsByPage with authorized user
		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.OPERATION_CONTROLLER_GET_OPERATIONS_BY_PAGE.replace(ControllerAPI.REQUEST_PAGE_NUMBER, String.valueOf(1)).replace(ControllerAPI.REQUEST_PAGE_SIZE, String.valueOf(10)))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGES), String.valueOf(5));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_PAGE), String.valueOf(1));
		assertEquals(response.getHeader(ControllerAPI.HEADER_VARIABLE_TOTAL), String.valueOf(42));
	}

	private void createOperations(int count) {
		for (int i = 0; i < count; i++) {
			Operation operation = new Operation().setInfo(new Info().setNamedRus(RandomStringUtils.randomAlphanumeric(10)));
			operationRepository.save(operation);
		}
	}

	@Test
	void getOperationById() throws Exception {
		assertEquals(Collections.emptyList(), operationRepository.findAll());
		int count = operationRepository.findAll().size();
		Operation save = operationRepository.save(testOperation);
		assertEquals(count + 1, operationRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString())).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var operation = JacksonUtils.fromJson(Operation.class, contentAsString);
		assertEquals(save, operation);

	}

	@Test
	void getOperationByIdWithIncorrectValue() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, "34342"))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void postOperation() throws Exception {
		OperationBean bean = createOperationBean();
		String json = JacksonUtils.getJson(bean);
		int count = operationRepository.findAll().size();
		// try with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		// try with user role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isCreated());
		count++;
		// try with judge role
		mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try with admin role
		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST).contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();

		assertEquals(count + 1, operationRepository.findAll().size());
		Operation operation = JacksonUtils.fromJson(Operation.class, contentAsString);

		assertEquals(bean.getInfo().getNamedRus(), operation.getInfo().getNamedRus());
		assertEquals(bean.getImagePath(), operation.getImagePath());

	}

	@Test
	void putOperation() throws Exception {
		int count = operationRepository.findAll().size();
		assertEquals(0, count);
		var bean = createOperationBean();
		var operation = operationService.postOperation(bean);
		assertEquals(count + 1, operationRepository.findAll().size());
		String json = JacksonUtils.getJson(bean);
		// try with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, operation.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try with user role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, operation.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try with judge role
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, operation.getId().toString()))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try with admin role
		String contentAsString = mockMvc
				.perform(MockMvcRequestBuilders.put(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, operation.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(count + 1, operationRepository.findAll().size());

		var operation1 = JacksonUtils.fromJson(Operation.class, contentAsString);

		assertEquals(operation.getId(), operation1.getId());
	}

	@Test
	void putOperationWithIncorrectValue() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, "456547654654"))
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(JacksonUtils.getJson(null)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void deleteOperationById() throws Exception {
		int actual = operationRepository.findAll().size();
		assertEquals(0, actual);

		Operation save = operationRepository.save(testOperation);
		assertEquals(actual + 1, operationRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		save = operationRepository.save(testOperation);

		// try access with admin role
		mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		assertEquals(actual, operationRepository.findAll().size());
	}

	@Test
	void getOperationCombatListHeaders() throws Exception {

		operationService.clearTypes();

		assertEquals(Collections.emptyList(), operationRepository.findAll());
		int count = operationRepository.findAll().size();
		Operation save = operationRepository.save(testOperation);
		assertEquals(count + 1, operationRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, judgeToken))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var list = JacksonUtils.getListFromJson(OperationCombatListHeaderBean[].class, contentAsString);
		assertEquals(1, list.size());

		weaponTypeRepository.save(new WeaponType().setName("AK-47"));
		weaponTypeRepository.save(new WeaponType().setName("AK-74"));

		contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		list = JacksonUtils.getListFromJson(OperationCombatListHeaderBean[].class, contentAsString);
		assertEquals(3, list.size());

	}

	@Test
	void getOperationCombatListData() throws Exception {

		operationService.clearTypes();

		assertEquals(Collections.emptyList(), operationRepository.findAll());
		int count = operationRepository.findAll().size();
		Operation save = operationRepository.save(testOperation);
		assertEquals(count + 1, operationRepository.findAll().size());

		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString())))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access with judge role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

		// try access with admin role
		String contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var list = JacksonUtils.getListFromJson(OperationCombatListHeaderBean[].class, contentAsString);
		assertEquals(0, list.size());

		weaponTypeRepository.save(new WeaponType().setName("AK-47"));
		weaponTypeRepository.save(new WeaponType().setName("AK-74"));

		contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		var doubleList = JacksonUtils.getListFromJson(List[].class, contentAsString);
		assertEquals(0, doubleList.size());

		var testPerson = personRepository.save(new Person().setName("Thor"));
		testOperation.setParticipants(Arrays.asList(testPerson));
		operationRepository.save(testOperation);

		contentAsString = mockMvc.perform(
				MockMvcRequestBuilders.get(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		doubleList = JacksonUtils.getListFromJson(List[].class, contentAsString);
		assertEquals(1, doubleList.size());
		assertEquals(3, doubleList.get(0).size());

		
		
	}
	
	@Test
	void setOperationCombatElements() throws Exception {

		Operation save = operationRepository.save(testOperation);
		
		var element = new OperationCombatElementBean().setCommander(testing.getId()).setCallSign("First").setName("Very first");
		element.getParticipants().add(testing.getId());
			

		var json = JacksonUtils.getJson(Arrays.asList(element));
		
		log.info("Json is %s", json);
		
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMBAT_ELEMENTS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMBAT_ELEMENTS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
	
	@Test
	public void setCommandantServices() throws Exception {
		
		Operation save = operationRepository.save(testOperation);
		
		
		var service = new OperationCommandantServiceBean().setCommandant(testing.getId()).setDistrictNumber("First");
		
		
		var json = JacksonUtils.getJson(Arrays.asList(service));
		
		log.info("Json is %s", json);
		
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMMANDANT_SERVICES.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMMANDANT_SERVICES.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void setParticipants() throws Exception {
		
		Operation save = operationRepository.save(testOperation);
		
		var json = JacksonUtils.getJson(Arrays.asList(testing.getId()));
		
		log.info("Json is %s", json);
		
		// try access with unauthorized user
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_PARTICIPANTS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

		// try access with user role
		mockMvc.perform(
				MockMvcRequestBuilders.post(ControllerAPI.OPERATION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_PARTICIPANTS.replace(ControllerAPI.REQUEST_OPERATION_ID, save.getId().toString()))
						.contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	private OperationBean createOperationBean() {
		return new OperationBean().setInfo(new Info().setNamedRus("Test")).setImagePath("test image path");
	}

}
