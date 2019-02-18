package tech.shooting.ipsc.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.User;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Test 
	public void checkFindByFields() {
		userRepository.save(new User().setLastName("Суворов").setFirstName("Антон").setMiddleName("Исхакович"));
		
		assertNotNull(userRepository.findByLastName("Суворов"));
		assertNotNull(userRepository.findByFirstName("Антон"));
	}

}
