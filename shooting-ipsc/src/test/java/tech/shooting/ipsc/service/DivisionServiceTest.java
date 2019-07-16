package tech.shooting.ipsc.service;

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
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.repository.DivisionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = DivisionRepository.class)
@ContextConfiguration(classes = { DivisionService.class, IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class DivisionServiceTest {
	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private DivisionService divisionService;

	@BeforeEach
	public void before() {
		divisionRepository.deleteAll();
	}

	@Test
	void createDivision() {

		var root = divisionRepository.createIfNotExists(new Division().setName("root").setParent(null));
		// try create division
		DivisionBean divisionBean = new DivisionBean().setName("Все").setParent(root.getId());
		DivisionBean division = divisionService.createDivision(divisionBean, root.getId());
		assertEquals(division.getName(), divisionBean.getName());
		assertEquals(division.getParent(), divisionBean.getParent());

		long count = divisionRepository.count();
		assertThrows(ValidationException.class, () -> divisionService.createDivision(divisionBean, root.getId()));
		assertEquals(count, divisionRepository.count());
		
		
	}
}