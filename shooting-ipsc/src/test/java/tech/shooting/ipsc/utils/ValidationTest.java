package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.pojo.Vehicle;

@Slf4j
public class ValidationTest {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private static SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);

	@Test
	public void checkVehicle() throws MethodArgumentNotValidException {
		assertThrows(MethodArgumentNotValidException.class, () -> validate(new Vehicle()));
	}

	private void validate(Vehicle value) throws MethodArgumentNotValidException {
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(value, "operation");
		springValidator.validate(value, errors, new Object[0]);
		if (errors.hasErrors()) {
			try {
				throw new MethodArgumentNotValidException(new MethodParameter(this.getClass().getDeclaredMethod("validate", Vehicle.class), 0), errors);
			} catch (NoSuchMethodException | SecurityException e) {
				log.error("Cannot find proper method");
				log.error("", e);
			}
		}
	}
}
