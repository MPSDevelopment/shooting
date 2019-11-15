package tech.shooting.ipsc.controller;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

public class BaseControllerTest {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected TokenUtils tokenUtils;

	protected User user;

	protected User admin;

	protected User judge;

	protected String adminToken;

	protected String judgeToken;

	protected String userToken;

	@BeforeEach
	public void before() {

		String password = RandomStringUtils.randomAscii(14);
		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword(password).setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);

		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

}
