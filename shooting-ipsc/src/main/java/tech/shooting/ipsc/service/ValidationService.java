package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;

import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import tech.shooting.commons.annotation.ValiationExportable;
import tech.shooting.ipsc.bean.ValidationBean;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ValidationService {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public Map<String, List<ValidationBean>> getConstraintsForPackage(String... packageNames) {
		var result = new HashMap<String, List<ValidationBean>>();
		Reflections reflections = new Reflections(packageNames);
		var classes = reflections.getSubTypesOf(ValiationExportable.class);
		classes.forEach(clazz -> {
			log.info("Class is %s", clazz);
			var constraints = getConstraints(clazz);
			if (!constraints.isEmpty()) {
				result.put(clazz.getSimpleName(), constraints);
			}
		});
		return result;
	}

	public List<ValidationBean> getConstraints(Class clazz) {
		BeanDescriptor descriptor = validator.getConstraintsForClass(clazz);
		log.info("Descriptor is %s", descriptor);
		var validationBeans = new ArrayList<ValidationBean>();
		descriptor.getConstrainedProperties().forEach(property -> {
			String propertyName = property.getPropertyName();

			var validationBean = new ValidationBean();

			PropertyDescriptor constraints = descriptor.getConstraintsForProperty(propertyName);
			constraints.getConstraintDescriptors().forEach(constraint -> {
				Annotation annotation = constraint.getAnnotation();
				log.info("Constraint is %s %s", annotation.getClass().getSimpleName(), constraint.getAnnotation());
				// log.info("Annotation is %s", JacksonUtils.getFullJson(constraint.getAnnotation()));
				if (annotation instanceof javax.validation.constraints.Size) {
					validationBean.setFieldName(propertyName).setMin((long) ((javax.validation.constraints.Size) annotation).min()).setMax((long) ((javax.validation.constraints.Size) annotation).max());
				} else if (annotation instanceof javax.validation.constraints.Max) {
					validationBean.setFieldName(propertyName).setMax(((javax.validation.constraints.Max) annotation).value());
				} else if (annotation instanceof javax.validation.constraints.Min) {
					validationBean.setFieldName(propertyName).setMin(((javax.validation.constraints.Min) annotation).value());
				} else if (annotation instanceof javax.validation.constraints.NotBlank) {
					validationBean.setFieldName(propertyName).setNotBlank(true);
				} else if (annotation instanceof javax.validation.constraints.NotEmpty) {
					validationBean.setFieldName(propertyName).setNotEmpty(true);
				} else if (annotation instanceof javax.validation.constraints.Null) {
					validationBean.setFieldName(propertyName).setNotNull(false);
				} else if (annotation instanceof javax.validation.constraints.NotNull) {
					validationBean.setFieldName(propertyName).setNotNull(true);
				} else if (annotation instanceof javax.validation.constraints.Positive) {
					validationBean.setFieldName(propertyName).setMin(1L);
				} else if (annotation instanceof javax.validation.constraints.PositiveOrZero) {
					validationBean.setFieldName(propertyName).setMin(0L);
				} else if (annotation instanceof javax.validation.constraints.Pattern) {
					validationBean.setFieldName(propertyName).setPattern(((javax.validation.constraints.Pattern) annotation).regexp());
				} else {
					log.error("Unrecognizable constraint annotation %s", annotation);
				}
			});

			validationBeans.add(validationBean);

		});
		return validationBeans;
	}
}
