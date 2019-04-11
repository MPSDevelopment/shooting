package tech.shooting.ipsc.utils;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.mongo.BaseDocument;

@Slf4j
public class ReflectionUtils {

	public static Set<Class<? extends Object>> getClasses(String... packageNames) {
		Reflections reflections = new Reflections(packageNames, new SubTypesScanner(false));
		var classes = reflections.getSubTypesOf(Object.class);
		classes.addAll(new Reflections(packageNames).getSubTypesOf(BaseDocument.class));
		log.info("There is %s classes", classes.size());
		return classes;
	}

}
