package tech.shooting.ipsc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {

	String message() default "{Incorrect password}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	// String[] patterns();

}