package tech.shooting.ipsc.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.repository.SettingsRepository;
import tech.shooting.ipsc.service.SettingsService;
import tech.shooting.ipsc.service.TagService;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses = SettingsRepository.class)
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, TagService.class, SettingsService.class, TagController.class })
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void checkGetStatus() throws Exception {
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STATUS)).andExpect(MockMvcResultMatchers.status().isOk());
	}
}