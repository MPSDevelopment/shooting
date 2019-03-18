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
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.repository.DivisionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = DivisionRepository.class)
@ContextConfiguration(classes = {DivisionService.class})
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
	public void before () {
		divisionRepository.deleteAll();
	}

	@Test
	void createDivision () {
		//try create division
		DivisionBean divisionBean = new DivisionBean().setName("dsdsdsds").setParent(null);
		Division division = divisionService.createDivision(divisionBean, null);
		assertEquals(division.getName(), divisionBean.getName());
		assertEquals(division.getParent(), divisionBean.getParent());
	}
}