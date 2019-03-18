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
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class DivisionRepositoryTest {
	@Autowired
	private DivisionRepository divisionRepository;

	@BeforeEach
	public void before () {
		divisionRepository.deleteAll();
	}

	@Test
	public void checkFindByNameAndParentId () {
		String name = "root";
		int count = divisionRepository.findAll().size();
		assertEquals(count, 0);
		log.info("Create root division");
		divisionRepository.save(new Division().setParent(null).setName(name));
		assertEquals(count + 1, divisionRepository.findAll().size());
		assertNotNull(divisionRepository.findByNameAndParent(name, null));
	}
}
