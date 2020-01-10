package tech.shooting.tag.service;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Hex;
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

import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.event.TagDetectedEvent;
import tech.shooting.tag.eventbus.EventBus;
import tech.shooting.tag.repository.SettingsRepository;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Slf4j
//@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { SettingsRepository.class, SettingsService.class, TagService.class, RunningService.class })
public class TagServiceTest {
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private RunningService runningService;

	@Test
	public void checkToHex() {
		int code = 11759;
		
		String hex = Integer.toHexString(code);

		// String hex = Hex.encodeHexString(code.getBytes());
		 assertEquals("2def", hex);
	}
	
	@Test 
	public void checkLaps() {
		tagService.startSending(4);
		
		long timeMillis = System.currentTimeMillis();
		
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		EventBus.publishEvent(new TagDetectedEvent("1111").setSending(true).setTime(timeMillis));
		
		assertEquals(4, runningService.getPersonData("1111").getLaps());
		
	}
}
