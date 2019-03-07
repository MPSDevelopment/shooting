package tech.shooting.ipsc.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@ContextConfiguration(classes = { IpscMongoConfig.class, UserDao.class, DatabaseCreator.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void checkUpsert() {
		long count = userRepository.count();

		String login = RandomStringUtils.randomAlphabetic(16);
		userDao.upsert(new User().setLogin(login));
		assertEquals(count + 1, userRepository.count());

		userDao.upsert(new User().setLogin(login));
		assertEquals(count + 1, userRepository.count());

		userRepository.findAll().forEach(user -> {
			log.info("User is %s", JacksonUtils.getFullJson(user));
		});

	}
}
