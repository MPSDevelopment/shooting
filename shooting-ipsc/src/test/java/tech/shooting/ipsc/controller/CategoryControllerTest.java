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
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CategoriesRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.UnitsRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.CategoryService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CategoriesRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CategoryController.class, CategoryService.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class CategoryControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitsRepository unitsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    private User user;

    private User admin;

    private User judge;

    private String adminToken;

    private String judgeToken;

    private String userToken;

    private Units testUnit;

    private Categories testCategory;

    private Subject testSubject;

    private Standard testStandard;


    @BeforeEach
    public void before() {
        categoriesRepository.deleteAll();

        testUnit = testUnit == null ? unitsRepository.save(new Units().setUnits("testUnits")) : testUnit;
        testCategory = testCategory == null ? categoriesRepository.save(new Categories().setNameCategoryRus("Vodka").setNameCategoryKz("Weapon Training")) : testCategory;
        testSubject = testSubject == null ? subjectRepository.save(new Subject().setRus("LitrBooool").setKz("Physical training")) : testSubject;
        testStandard = new Standard().setActive(true).setSubject(testSubject).setGroups(false).setInfo(new Info().setNamedRus("Бег с припятствиями за водкой").setNamedKz("Running with obstacles").setDescriptionRus("Бла бла бла бла бла").setDescriptionKz("Возраст. Спортсмен может получить определенный разряд только при условии достижения им определенного возраста: с 10 лет — 1-3 юношеские разряды и взрослые разряды, с 14 лет — КМС, с 15 лет — МС, а с 16 лет — МСМК."));

        String password = RandomStringUtils.randomAscii(14);
        user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
        admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
        judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

        userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
    }




    @Test
    void checkGetCategories() throws Exception {
        assertEquals(1,categoriesRepository.findAll().size());
        //try access with unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

        //try access with user role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());

        //try access with judge role
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());

        //try access with admin role
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        List<Categories> listFromJson = JacksonUtils.getListFromJson(Categories[].class, contentAsString);
        assertEquals(testCategory, listFromJson.get(0));
    }

}