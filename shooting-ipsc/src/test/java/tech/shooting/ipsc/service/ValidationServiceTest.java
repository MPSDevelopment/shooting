package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.UserLogin;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
//@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, WebMvcAutoConfiguration.class})
//@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
@SpringBootTest(classes = { ValidationService.class })
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
@ActiveProfiles("simple")
public class ValidationServiceTest {
	
	@Autowired
	private ValidationService validationService;

	@Test
	public void checkConstraintsForClass() {
//		var result = validationService.getConstraints(UserLogin.class);
//		log.info("Constraints are %s", JacksonUtils.getPrettyJson(result));
		assertEquals(3, validationService.getConstraints(UserSignupBean.class).size());
		assertEquals(2, validationService.getConstraints(UserLogin.class).size());
		assertEquals(2, validationService.getConstraints(ChangePasswordBean.class).size());
		assertEquals(3, validationService.getConstraints(CompetitionBean.class).size());
		assertEquals(2, validationService.getConstraints(UserUpdateBean.class).size());
	}

	@Test
	public void checkConstraintsForPackage() {
		var result = validationService.getConstraintsForPackage("tech.shooting.ipsc.bean", "tech.shooting.ipsc.pojo");
		log.info("Constraints are %s", JacksonUtils.getPrettyJson(result));
		assertTrue(result.size() > 8);
	}
}
