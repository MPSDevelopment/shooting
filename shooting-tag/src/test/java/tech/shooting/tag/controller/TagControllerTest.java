package tech.shooting.tag.controller;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.repository.SettingsRepository;
import tech.shooting.tag.service.SettingsService;
import tech.shooting.tag.service.TagService;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
//@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { SettingsRepository.class, SettingsService.class, TagService.class, TagController.class })
class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void checkGetStatus() throws Exception {
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STATUS)).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void checkGetMode() throws Exception {
		// try access with unauthorized user role
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_MODE)).andExpect(MockMvcResultMatchers.status().isBadGateway());
	}
}