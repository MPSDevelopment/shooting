package tech.shooting.ipsc;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.annotation.ValiationExportable;

@Slf4j
public class ReflectionsTest {

	@Test
	public void checkReflections() {
		Reflections reflections = new Reflections("tech.shooting.ipsc.bean", "tech.shooting.ipsc.pojo");
		var classes = reflections.getSubTypesOf(ValiationExportable.class);
		classes.forEach(clazz -> {
			log.info("Class is %s", clazz);
		});
	}
}
