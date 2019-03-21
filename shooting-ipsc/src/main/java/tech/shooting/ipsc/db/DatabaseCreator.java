package tech.shooting.ipsc.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.User;

import javax.annotation.PostConstruct;

@Component
public class DatabaseCreator {
	public static final String ADMIN_PASSWORD = "test";

	public static final String JUDGE_PASSWORD = "judgeTest";

	public static final String ADMIN_LOGIN = "admin";

	public static final String JUDGE_LOGIN = "judge";

	@Autowired
	private DatabaseCreator databaseCreator;

	@Autowired
	private UserDao userDao;

	public DatabaseCreator () {
	}

	@PostConstruct
	public void init () {
		databaseCreator.createDatabase();
	}

	private void createDatabase () {
		userDao.createIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(ADMIN_PASSWORD).setRoleName(RoleName.ADMIN).setActive(true).setName("Admin"));
		userDao.createIfNotExists(new User().setLogin(JUDGE_LOGIN).setPassword(JUDGE_PASSWORD).setRoleName(RoleName.JUDGE).setActive(true).setName("Judge"));
	}
}
