package tech.shooting.ipsc;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.annotation.ValiationExportable;
import tech.shooting.commons.constraints.IpscConstants;

@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class ReflectionsTest {

	@Test
	public void checkReflections() {
		Reflections reflections = new Reflections("tech.shooting.ipsc.bean", "tech.shooting.ipsc.pojo");
		var classes = reflections.getSubTypesOf(ValiationExportable.class);
		classes.forEach(clazz -> {
			log.info("Class is %s", clazz);
		});
		assertTrue(classes.size() > 15);
	}
}
