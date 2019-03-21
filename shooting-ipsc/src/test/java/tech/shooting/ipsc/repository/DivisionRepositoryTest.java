package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.util.List;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class DivisionRepositoryTest {

	private static final String ROOT_NAME = "root";
	@Autowired
	private DivisionRepository divisionRepository;
	private Division root;

	@BeforeEach
	public void before() {
		divisionRepository.deleteAll();
		int count = divisionRepository.findAll().size();
		log.info("Create root division");
		root = divisionRepository.save(new Division().setParent(null).setName(ROOT_NAME));
		assertEquals(count + 1, divisionRepository.findAll().size());
	}

	@Test
	public void checkFindByNameAndParentId() {
		assertNotNull(divisionRepository.findByNameAndParent(ROOT_NAME, null));
	}

	@Test
	@Disabled
	public void check() {
		Division child1 = divisionRepository.save(new Division().setParent(root).setName("child1"));
		List<Division> list = divisionRepository.findByDivisionId(root.getId());

		assertEquals(2, list.size());
		
		Division child2 = divisionRepository.save(new Division().setParent(root).setName("child2"));
		
		Division grandChild1 = divisionRepository.save(new Division().setParent(child1).setName("grandChild1"));
		Division grandChild2 = divisionRepository.save(new Division().setParent(child1).setName("grandChild2"));
		Division grandChild3 = divisionRepository.save(new Division().setParent(child2).setName("grandChild3"));
		
		list = divisionRepository.findByDivisionId(root.getId());
		assertEquals(6, list.size());
		
		list = divisionRepository.findByDivisionId(child1.getId());
		assertEquals(3, list.size());
		
		list = divisionRepository.findByDivisionId(child2.getId());
		assertEquals(2, list.size());
		
	}
}
