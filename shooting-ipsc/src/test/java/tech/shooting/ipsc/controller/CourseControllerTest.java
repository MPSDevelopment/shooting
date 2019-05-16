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
import tech.shooting.ipsc.bean.CourseBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CourseRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.CourseService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CourseRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CourseController.class, CourseService.class})
class CourseControllerTest {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private MockMvc mockMvc;

    private Division testDivision;

    private User user;

    private User admin;

    private User judge;

    private String adminToken;

    private String judgeToken;

    private String userToken;

    private Person testing;


    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        WeaponIpscCode weaponIpscCode = new WeaponIpscCode().setCode("445645645").setTypeWeapon(WeaponTypeEnum.HANDGUN);
        List<WeaponIpscCode> codes = new ArrayList<>();
        codes.add(weaponIpscCode);
        testDivision = testDivision == null ? divisionRepository.save(new Division().setParent(null).setName("Root").setActive(true)) : testDivision;
        testing = personRepository.save(new Person().setName("testing").setCodes(codes).setQualifierRank(ClassificationBreaks.D).setDivision(testDivision));
        user = user == null
                ? userRepository.save(
                new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("dfhhjsdgfdsfhj").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150")).setPerson(testing))
                : user;
        admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
        judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
        userToken = tokenUtils.createToken(user.getId(), Token.TokenType.USER, user.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
    }

    @Test
    void checkGetCourseByDivision() throws Exception {
        assertEquals(0, courseRepository.findAll().size());

        CourseBean bean = new CourseBean().setPerson(testing.getId()).setName("bla bla").setImagePath("fdgdsfdsfsdfsd").setAddress("fsdfds").setDate(OffsetDateTime.now());
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_POST_COURSE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JacksonUtils.getJson(bean)).header(Token.TOKEN_HEADER, adminToken))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
        Course course = JacksonUtils.fromJson(Course.class, contentAsString);

        assertEquals(1, courseRepository.findAll().size());
        //unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION
                .replace(ControllerAPI.REQUEST_DIVISION_ID, course.getDivision().toString()))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());

        //user role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION
                .replace(ControllerAPI.REQUEST_DIVISION_ID, course.getDivision().toString()))
                .header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
        //judge role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION
                .replace(ControllerAPI.REQUEST_DIVISION_ID, course.getDivision().toString()))
                .header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

        String contentAsString1 = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.COURSE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION
                .replace(ControllerAPI.REQUEST_DIVISION_ID, course.getDivision().toString()))
                .header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        List<Course> listFromJson = JacksonUtils.getListFromJson(Course[].class, contentAsString1);
        assertEquals(course.getId(), listFromJson.get(0).getId());

    }
}