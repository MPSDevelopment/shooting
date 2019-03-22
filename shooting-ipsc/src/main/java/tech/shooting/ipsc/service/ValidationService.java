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
			PropertyDescriptor constraints = descriptor.getConstraintsForProperty(propertyName);
			constraints.getConstraintDescriptors().forEach(constraint -> {
				Annotation annotation = constraint.getAnnotation();
				log.info("Constraint is %s %s", annotation.getClass().getSimpleName(), constraint.getAnnotation());
				// log.info("Annotation is %s", JacksonUtils.getFullJson(constraint.getAnnotation()));
				if (annotation instanceof javax.validation.constraints.Size) {
					validationBeans.add(new ValidationBean().setName("Size").setFieldName(propertyName).setMessage(((javax.validation.constraints.Size) annotation).message()).setMin(((javax.validation.constraints.Size) annotation).min())
							.setMax(((javax.validation.constraints.Size) annotation).max()));
				} else if (annotation instanceof javax.validation.constraints.Max) {
					validationBeans.add(new ValidationBean().setName("Max").setFieldName(propertyName).setMessage(((javax.validation.constraints.Max) annotation).message()).setValue(((javax.validation.constraints.Max) annotation).value()));
				} else if (annotation instanceof javax.validation.constraints.Min) {
					validationBeans.add(new ValidationBean().setName("Min").setFieldName(propertyName).setMessage(((javax.validation.constraints.Min) annotation).message()).setValue(((javax.validation.constraints.Min) annotation).value()));
				} else if (annotation instanceof javax.validation.constraints.NotBlank) {
					validationBeans.add(new ValidationBean().setName("NotBlank").setFieldName(propertyName).setMessage(((javax.validation.constraints.NotBlank) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.NotEmpty) {
					validationBeans.add(new ValidationBean().setName("NotEmpty").setFieldName(propertyName).setMessage(((javax.validation.constraints.NotEmpty) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.Null) {
					validationBeans.add(new ValidationBean().setName("Null").setFieldName(propertyName).setMessage(((javax.validation.constraints.Null) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.NotNull) {
					validationBeans.add(new ValidationBean().setName("NotNull").setFieldName(propertyName).setMessage(((javax.validation.constraints.NotNull) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.Positive) {
					validationBeans.add(new ValidationBean().setName("Min").setFieldName(propertyName).setMin(1).setMessage(((javax.validation.constraints.Positive) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.PositiveOrZero) {
					validationBeans.add(new ValidationBean().setName("Min").setFieldName(propertyName).setMin(0).setMessage(((javax.validation.constraints.PositiveOrZero) annotation).message()));
				} else if (annotation instanceof javax.validation.constraints.Pattern) {
					validationBeans.add(new ValidationBean().setName("Pattern").setFieldName(propertyName).setMessage(((javax.validation.constraints.Pattern) annotation).message())
							.setPattern(((javax.validation.constraints.Pattern) annotation).regexp()));
				} else {
					log.error("Unrecognizable constraint annotation %s", annotation);
				}
			});
		});
		return validationBeans;
	}
}
