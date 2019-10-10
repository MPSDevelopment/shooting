//package tech.shooting.ipsc.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.time.DateUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//import org.springframework.http.MediaType;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import tech.shooting.commons.constraints.IpscConstants;
//import tech.shooting.commons.enums.RoleName;
//import tech.shooting.commons.pojo.Token;
//import tech.shooting.commons.utils.JacksonUtils;
//import tech.shooting.commons.utils.TokenUtils;
//import tech.shooting.ipsc.advice.ValidationErrorHandler;
//import tech.shooting.ipsc.config.IpscMongoConfig;
//import tech.shooting.ipsc.config.IpscSettings;
//import tech.shooting.ipsc.config.SecurityConfig;
//import tech.shooting.ipsc.db.DatabaseCreator;
//import tech.shooting.ipsc.db.UserDao;
//import tech.shooting.ipsc.pojo.Address;
//import tech.shooting.ipsc.pojo.Category;
//import tech.shooting.ipsc.pojo.User;
//import tech.shooting.ipsc.repository.CategoriesRepository;
//import tech.shooting.ipsc.repository.UserRepository;
//import tech.shooting.ipsc.service.CategoryService;
//
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(SpringExtension.class)
//@EnableMongoRepositories(basePackageClasses = CategoriesRepository.class)
//@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CategoryController.class, CategoryService.class})
//@EnableAutoConfiguration
//@AutoConfigureMockMvc
//@SpringBootTest
//@DirtiesContext
//@Slf4j
//@Tag(IpscConstants.UNIT_TEST_TAG)
//class CategoryControllerTest {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CategoriesRepository categoriesRepository;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TokenUtils tokenUtils;
//
//    private User user;
//
//    private User admin;
//
//    private User judge;
//
//    private String adminToken;
//
//    private String judgeToken;
//
//    private String userToken;
//
//    private Category testCategory;
//
//
//    @BeforeEach
//    public void before() {
//        categoriesRepository.deleteAll();
//        testCategory = testCategory == null ? categoriesRepository.save(new Category().setNameCategoryRus("Vodka").setNameCategoryKz("Weapon Training")) : testCategory;
//        String password = RandomStringUtils.randomAscii(14);
//        user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
//        admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
//        judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
//
//        userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
//        adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
//        judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
//    }
//
//
//    @Test
//    void checkGetCategories() throws Exception {
//        assertEquals(1, categoriesRepository.findAll().size());
//        //try access with unauthorized user
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
//
//        //try access with user role
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
//
//        //try access with judge role
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//
//        //try access with admin role
//        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_ALL_CATEGORIES).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
//        List<Category> listFromJson = JacksonUtils.getListFromJson(Category[].class, contentAsString);
//        assertEquals(testCategory, listFromJson.get(0));
//    }
//
//    @Test
//    void checkGetCategoryById() throws Exception {
//        assertEquals(1, categoriesRepository.findAll().size());
//        //try access with unauthorized user
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_CATEGORY_BY_ID.replace(ControllerAPI.REQUEST_CATEGORY_ID, testCategory.getId().toString()))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
//
//        //try access with user role
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_CATEGORY_BY_ID.replace(ControllerAPI.REQUEST_CATEGORY_ID, testCategory.getId().toString())).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk());
//
//        //try access with judge role
//        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_CATEGORY_BY_ID.replace(ControllerAPI.REQUEST_CATEGORY_ID, testCategory.getId().toString())).header(Token.TOKEN_HEADER, judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//
//        //try access with admin role
//        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_GET_CATEGORY_BY_ID.replace(ControllerAPI.REQUEST_CATEGORY_ID, testCategory.getId().toString())).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
//        Category categories = JacksonUtils.fromJson(Category.class, contentAsString);
//        assertEquals(testCategory, categories);
//    }
//
//    @Test
//    void checkPostCategory() throws Exception {
//        assertEquals(1, categoriesRepository.findAll().size());
//        Category category = new Category().setNameCategoryRus("птн пнх").setNameCategoryKz("Pytin is an enemy");
//        String json = JacksonUtils.getJson(category);
//        //try access with unauthorized user
//        mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_POST_CATEGORY)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
//        //try access with judge role
//        mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_POST_CATEGORY)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, judgeToken))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//        //try access with user role
//        mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_POST_CATEGORY)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, userToken))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//        //try access with admin role
//        mockMvc.perform(MockMvcRequestBuilders.post(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_POST_CATEGORY)
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, adminToken))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//
//    }
//
//    @Test
//    void checkPutCategory() throws Exception {
//        assertEquals(1, categoriesRepository.findAll().size());
//        Category category = new Category().setNameCategoryRus("птн пнх").setNameCategoryKz("Pytin is an enemy");
//        Category save = categoriesRepository.save(category);
//        category.setNameCategoryRus("птн пнх 10 ").setNameCategoryKz("Pytin is an enemy all the word");
//        String json = JacksonUtils.getJson(category);
//
//        //try access with unauthorized user
//        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_PUT_CATEGORY.replace(ControllerAPI.REQUEST_CATEGORY_ID,save.getId().toString()))
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
//
//        //try access with user role
//        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_PUT_CATEGORY.replace(ControllerAPI.REQUEST_CATEGORY_ID,save.getId().toString()))
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, userToken))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//
//        //try access with judge role
//        mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_PUT_CATEGORY.replace(ControllerAPI.REQUEST_CATEGORY_ID,save.getId().toString()))
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, judgeToken))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//
//        //try access with admin role
//        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.put(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_PUT_CATEGORY.replace(ControllerAPI.REQUEST_CATEGORY_ID, save.getId().toString()))
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json).header(Token.TOKEN_HEADER, adminToken))
//                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
//        Category categories = JacksonUtils.fromJson(Category.class, contentAsString);
//        assertEquals(category.getNameCategoryKz(),categories.getNameCategoryKz());
//        assertEquals(category.getNameCategoryRus(),categories.getNameCategoryRus());
//        assertEquals(category.getId(),categories.getId());
//    }
//
//    @Test
//    void checkDeleteCategory() throws Exception{
//        assertEquals(1, categoriesRepository.findAll().size());
//
//        //try access with unauthorized user
//        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_DELETE_CATEGORY_BY_ID
//                .replace(ControllerAPI.REQUEST_CATEGORY_ID,testCategory.getId().toString()))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
//        //try access with  user role
//        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_DELETE_CATEGORY_BY_ID
//                .replace(ControllerAPI.REQUEST_CATEGORY_ID,testCategory.getId().toString())).header(Token.TOKEN_HEADER,userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//        //try access with  judge role
//        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_DELETE_CATEGORY_BY_ID
//                .replace(ControllerAPI.REQUEST_CATEGORY_ID,testCategory.getId().toString())).header(Token.TOKEN_HEADER,judgeToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//        //try access with  admin role
//        mockMvc.perform(MockMvcRequestBuilders.delete(ControllerAPI.CATEGORY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.CATEGORY_CONTROLLER_DELETE_CATEGORY_BY_ID
//                .replace(ControllerAPI.REQUEST_CATEGORY_ID,testCategory.getId().toString())).header(Token.TOKEN_HEADER,adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
//
//        assertEquals(0,categoriesRepository.findAll().size());
//
//    }
//
//}