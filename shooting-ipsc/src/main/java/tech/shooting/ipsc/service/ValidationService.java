package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;

import tech.shooting.commons.annotation.ValiationExportable;
import tech.shooting.ipsc.bean.ValidationBean;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ValidationService {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public Map<String, Map<String, ValidationBean>> getConstraintsForPackage(String... packageNames) {
		var result = new HashMap<String, Map<String, ValidationBean>>();
		Reflections reflections = new Reflections(packageNames);
		var classes = reflections.getSubTypesOf(ValiationExportable.class);
		classes.forEach(clazz -> {
			log.debug("Class is %s", clazz);
			var constraints = getConstraints(clazz);
			if (!constraints.isEmpty()) {
				result.put(clazz.getSimpleName(), constraints);
			}
		});
		return result;
	}

	public Map<String, ValidationBean> getConstraints(Class clazz) {
		BeanDescriptor descriptor = validator.getConstraintsForClass(clazz);
		log.debug("Descriptor is %s", descriptor);
		var validationBeans = new HashMap<String, ValidationBean>();
		descriptor.getConstrainedProperties().forEach(property -> {
			String propertyName = property.getPropertyName();
			String propertyJsonName = property.getPropertyName();
			String propertyType = property.getElementClass().getSimpleName();

			try {
				var field = clazz.getDeclaredField(propertyName);
				var annotation = field.getAnnotation(JsonProperty.class);
				if (StringUtils.isNotBlank(annotation.value())) {
					log.debug("Field name will be changed from %s to %s", propertyName, annotation.value());
					propertyJsonName = annotation.value();
				}
			} catch (NoSuchFieldException | SecurityException e) {
				log.error("Cannot find fieldName %s for class %s", propertyName, clazz);
				Arrays.asList(clazz.getDeclaredFields()).forEach(field -> log.info("Field is %s", field.getName()));
				e.printStackTrace();
			}

			var validationBean = new ValidationBean();

			PropertyDescriptor constraints = descriptor.getConstraintsForProperty(propertyName);
			constraints.getConstraintDescriptors().forEach(constraint -> {
				Annotation annotation = constraint.getAnnotation();
				log.debug("Constraint is %s %s", annotation.getClass().getSimpleName(), constraint.getAnnotation());
				// log.info("Annotation is %s", JacksonUtils.getFullJson(constraint.getAnnotation()));
				if (annotation instanceof javax.validation.constraints.Size) {
					int min = ((javax.validation.constraints.Size) annotation).min();
					int max = ((javax.validation.constraints.Size) annotation).max();
					validationBean.setMinLength(min == Integer.MIN_VALUE ? null : min).setMaxLength(max == Integer.MAX_VALUE ? null : max);
				} else if (annotation instanceof javax.validation.constraints.Max) {
					validationBean.setMax(((javax.validation.constraints.Max) annotation).value());
				} else if (annotation instanceof javax.validation.constraints.Min) {
					validationBean.setMin(((javax.validation.constraints.Min) annotation).value());
				} else if (annotation instanceof javax.validation.constraints.AssertTrue) {
					validationBean.setRequiredTrue(true);
				} else if (annotation instanceof javax.validation.constraints.AssertFalse) {
					validationBean.setRequiredFalse(true);
				} else if (annotation instanceof javax.validation.constraints.NotBlank) {
					validationBean.setRequired(true);
				} else if (annotation instanceof javax.validation.constraints.NotEmpty) {
					validationBean.setRequired(true);
				} else if (annotation instanceof javax.validation.constraints.Null) {
					validationBean.setRequired(false);
				} else if (annotation instanceof javax.validation.constraints.NotNull) {
					validationBean.setRequired(true);
				} else if (annotation instanceof javax.validation.constraints.Positive) {
					validationBean.setMin(1L);
				} else if (annotation instanceof javax.validation.constraints.PositiveOrZero) {
					validationBean.setMin(0L);
				} else if (annotation instanceof javax.validation.constraints.Pattern) {
					validationBean.setPattern(((javax.validation.constraints.Pattern) annotation).regexp());
				} else {
					log.error("Unrecognizable constraint annotation %s", annotation);
				}
			});

			validationBeans.put(propertyJsonName, validationBean);

		});
		return validationBeans;
	}
}
