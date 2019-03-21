package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.UserLogin;
import tech.shooting.ipsc.bean.UserUpdateBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ValidationService.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class ValidationServiceTest {
	
	@Autowired
	private ValidationService validationService;

	@Test
	public void checkConstraints() {

		var result = validationService.getConstraints(UserLogin.class);
		
		log.info("Constraints are %s", JacksonUtils.getPrettyJson(result));
		
		assertEquals(3, validationService.getConstraints(UserLogin.class).size());
		assertEquals(3, validationService.getConstraints(ChangePasswordBean.class).size());
		assertEquals(4, validationService.getConstraints(CompetitionBean.class).size());
		assertEquals(3, validationService.getConstraints(UserUpdateBean.class).size());
		
	}
}
