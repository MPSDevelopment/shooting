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
import tech.shooting.ipsc.bean.StandardCommonConditionsBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CommonConditionsRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.StandardCommonConditionsService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CommonConditionsRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, StandardCommonConditionsController.class, StandardCommonConditionsService.class})
class StandardCommonConditionsControllerTest {
    @Autowired
    private CommonConditionsRepository commonConditionsRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private UnitEnum units;
    
    private User user;

    private User admin;

    private User judge;

    private String adminToken;

    private String judgeToken;

    private String userToken;

    private Person testPerson;

    private StandardCommonConditions testCommonConditions;

    @BeforeEach
    void setUp() {
        commonConditionsRepository.deleteAll();
        testCommonConditions = new StandardCommonConditions().setUnits(units).setConditionsRus("fdsfdsfd").setConditionsKz("fsdfsdfds").setCoefficient(20.0);
        units = UnitEnum.TIMES_MORE;

        user = user == null
                ? userRepository.save(
                new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150")).setPerson(testPerson))
                : user;
        admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
        judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
        userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
    }

    @Test
    void checkPostCommonCondition() throws Exception {
        StandardCommonConditionsBean bean = new StandardCommonConditionsBean().setCoefficient(20.0)
                .setConditionsKz("fdsfdsfsfsd")
                .setConditionsRus("fdsdfsfds")
                .setUnits(units);
        String json = JacksonUtils.getJson(bean);

        mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_POST_CONDITION)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isCreated());

        assertEquals(1, commonConditionsRepository.findAll().size());
    }

    @Test
    void checkGetAllCommonConditions() throws Exception {
        assertEquals(0, commonConditionsRepository.findAll().size());
        commonConditionsRepository.save(new StandardCommonConditions().setUnits(units).setConditionsRus("fdsfdsfd").setConditionsKz("fsdfsdfds").setCoefficient(20.0));
        commonConditionsRepository.save(new StandardCommonConditions().setUnits(units).setConditionsRus("fdsfdsfd").setConditionsKz("fsdfsdfds").setCoefficient(20.0));
        //try access with unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_ALL))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        //try access with  user role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_ALL)
                .header(Token.TOKEN_HEADER, userToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
        //try access with  judge role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_ALL)
                .header(Token.TOKEN_HEADER, judgeToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        //try access with admin role
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_ALL)
                .header(Token.TOKEN_HEADER, adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        List<StandardCommonConditions> listFromJson = JacksonUtils.getListFromJson(StandardCommonConditions[].class, contentAsString);
        assertEquals(2, listFromJson.size());
    }

    @Test
    void checkGetCommonConditionById() throws Exception {
        assertEquals(0, commonConditionsRepository.findAll().size());
        StandardCommonConditions condition = commonConditionsRepository.save(testCommonConditions);

        //try access with unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        //try access with  user role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, userToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //try access with  judge role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, judgeToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        //try access with admin role
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_GET_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        StandardCommonConditions conditions = JacksonUtils.fromJson(StandardCommonConditions.class, contentAsString);

        assertEquals(condition, conditions);
        assertEquals(1, commonConditionsRepository.findAll().size());
    }

    @Test
    void checkDeleteCommonConditionById() throws Exception {
        assertEquals(0, commonConditionsRepository.findAll().size());
        StandardCommonConditions condition = commonConditionsRepository.save(testCommonConditions);
        assertEquals(1, commonConditionsRepository.findAll().size());

        //try access with unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        //try access with  user role
        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, userToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //try access with  judge role
        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, judgeToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

     condition = commonConditionsRepository.save(testCommonConditions);
        assertEquals(1, commonConditionsRepository.findAll().size());
        //try access with admin role
        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_DELETE_BY_ID.replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .header(Token.TOKEN_HEADER, adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(0, commonConditionsRepository.findAll().size());
    }

    @Test
    void checkPutCommonCondition() throws Exception {
        assertEquals(0, commonConditionsRepository.findAll().size());
        StandardCommonConditions condition = commonConditionsRepository.save(testCommonConditions);
        assertEquals(1, commonConditionsRepository.findAll().size());

        StandardCommonConditionsBean bean = new StandardCommonConditionsBean().setCoefficient(20.0)
                .setConditionsKz("fdsfdsfsfsd")
                .setConditionsRus("fdsdfsfds")
                .setUnits(units);
        String json = JacksonUtils.getJson(bean);

        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_PUT_CONDITION
                .replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_PUT_CONDITION
                .replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_PUT_CONDITION
                .replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_COMMON_CONDITION_CONTROLLER_PUT_CONDITION
                .replace(ControllerAPI.REQUEST_COMMON_CONDITION_ID, condition.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(json).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        StandardCommonConditions conditions = JacksonUtils.fromJson(StandardCommonConditions.class, contentAsString);
        assertEquals(bean.getCoefficient(), conditions.getCoefficient());
        assertEquals(bean.getConditionsKz(), conditions.getConditionsKz());
        assertEquals(bean.getConditionsRus(), conditions.getConditionsRus());


        assertEquals(1, commonConditionsRepository.findAll().size());
    }

}