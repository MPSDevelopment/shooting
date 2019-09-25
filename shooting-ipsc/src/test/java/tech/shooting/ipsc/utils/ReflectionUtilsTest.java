package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.utils.ReflectionUtils;

@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class ReflectionUtilsTest {

	@Test
	public void checkReflections() {
		Set<Class<? extends Object>> classes = ReflectionUtils.getClasses("tech.shooting.ipsc.pojo");
		log.info("There is %s classes", classes.size());
		classes.forEach(clazz -> {
			log.info("Class is %s", clazz);
		});
		assertTrue(classes.size() > 25);
		
		classes = ReflectionUtils.getClasses("tech.shooting.ipsc.bean");
		log.info("There is %s classes", classes.size());
		classes.forEach(clazz -> {
			log.info("Class is %s", clazz);
		});
		assertTrue(classes.size() > 25);
	}
}
