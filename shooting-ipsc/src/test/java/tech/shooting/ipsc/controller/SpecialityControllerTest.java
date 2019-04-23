package tech.shooting.ipsc.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Speciality;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.SpecialityRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = SpecialityRepository.class)
@ContextConfiguration(classes = {ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
        TokenAuthenticationFilter.class, IpscUserDetailsService.class, ValidationErrorHandler.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
class SpecialityControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

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

    @BeforeEach
    public void before() {
        specialityRepository.deleteAll();
        String password = RandomStringUtils.randomAscii(14);
        user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
        admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
        judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

        userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
        judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
    }

    @Disabled
    @Test
    void checkGetAllSpeciality() throws Exception {
        addSpecialityToDb(5);
        mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.SPECIALITY_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY).header(Token.TOKEN_HEADER, adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void addSpecialityToDb(int i) {
        for (int j = 0; j < i; j++) {
            specialityRepository.save(new Speciality().setSpecialityRus("Алкаш в поколении " + i).setSpecialityKz("Medic " + i));
        }
        assertEquals(specialityRepository.findAll().size(),i);
    }

}