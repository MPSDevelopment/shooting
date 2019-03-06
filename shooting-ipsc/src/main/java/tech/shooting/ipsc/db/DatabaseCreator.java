package tech.shooting.ipsc.db;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

@Component
public class DatabaseCreator {

	public static final String ADMIN_PASSWORD = "test";

	public static final String ADMIN_LOGIN = "admin";
	
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private DatabaseCreator databaseCreator;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDao userDao;

	public DatabaseCreator() {

	}

	@PostConstruct
	public void init() {
		databaseCreator.createDatabase();
	}

	private void createDatabase() {
		userDao.createIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(ADMIN_PASSWORD));
	}
}
