package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void before () {
		userRepository.deleteAll();
	}

	@Test
	public void checkFindByName () {
		userRepository.save(new User().setName("Суворов Антон"));
		assertNotNull(userRepository.findByName("Суворов Антон"));
	}

	@Test
	void checkFindByRoleName () {
		for(int i = 0; i < 50; i++) {
			if(i % 2 == 0) {
				userRepository.save(new User().setLogin("Andrey" + i).setName("Andrey" + i).setRoleName(RoleName.JUDGE));
			} else if(i == 15) {
				userRepository.save(new User().setLogin("Andrey" + i).setName("Andrey" + i).setRoleName(RoleName.ADMIN));
			} else {
				userRepository.save(new User().setLogin("Andrey" + i).setName("Andrey" + i).setRoleName(RoleName.USER));
			}
		}
		assertEquals(userRepository.findByRoleName(RoleName.ADMIN).size(), 1);
		assertEquals(userRepository.findByRoleName(RoleName.JUDGE).size(), 25);
		assertEquals(userRepository.findByRoleName(RoleName.USER).size(), 24);
	}

	@Test
	public void checkFindByLogin () {
		userRepository.save(new User().setName("Суворов Антон").setLogin("qwerty"));
		assertNotNull(userRepository.findByLogin("qwerty"));
	}

	@Test
	public void checkFindByLoginAndActive () {
		userRepository.save(new User().setName("Суворов Антон").setLogin("qwerty").setActive(true));
		assertNotNull(userRepository.findByLoginAndActive("qwerty", true));
		assertNull(userRepository.findByLoginAndActive("qwerty", false));
	}

	@Test
	public void checkDeleteByRoleName () {
		userRepository.save(new User().setName("Суворов Антон").setLogin("qwerty").setActive(true).setRoleName(RoleName.USER));
		userRepository.save(new User().setName("Суворов Антон").setLogin("qwerty1").setActive(true).setRoleName(RoleName.JUDGE));
		userRepository.save(new User().setName("Суворов Антон").setLogin("qwerty2").setActive(true).setRoleName(RoleName.ADMIN));
		userRepository.deleteByRoleName(RoleName.USER);
		assertEquals(Collections.EMPTY_LIST, userRepository.findByRoleName(RoleName.USER));
		assertNotNull(userRepository.findByRoleName(RoleName.JUDGE));
		assertNotNull(userRepository.findByRoleName(RoleName.ADMIN));
	}
}
