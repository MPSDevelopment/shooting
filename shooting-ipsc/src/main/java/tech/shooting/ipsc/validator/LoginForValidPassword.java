package tech.shooting.ipsc.validator;

import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginForValidPassword {

	String message() default "{Incorrect email}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	// String[] patterns();

}