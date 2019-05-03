package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import tech.shooting.ipsc.bean.ValidationBean;
import tech.shooting.ipsc.config.CachingConfig;
import tech.shooting.ipsc.utils.ReflectionUtils;
import tech.shooting.ipsc.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ValidationService {

	@Cacheable(value = CachingConfig.IPSC_CACHE, unless = "#result == null")
	public Map<String, Map<String, ValidationBean>> getConstraintsForPackage(String... packageNames) {
		var result = new HashMap<String, Map<String, ValidationBean>>();
		var classes = ReflectionUtils.getClasses(packageNames);
		classes.forEach(clazz -> {
			log.debug("Class is %s", clazz);
			var constraints = getConstraints(clazz);
			if (!constraints.isEmpty()) {
				result.put(clazz.getSimpleName(), constraints);
			}
		});
		return result;
	}

	protected Map<String, ValidationBean> getConstraints(Class<? extends Object> clazz) {
		return ValidationUtils.getConstraints(clazz);
	}
}
