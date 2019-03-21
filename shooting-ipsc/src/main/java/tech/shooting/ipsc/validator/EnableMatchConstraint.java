package tech.shooting.ipsc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchValidator.class)
@Documented
public @interface EnableMatchConstraint {
	String message () default "";

	Class<?>[] groups () default {};

	Class<? extends Payload>[] payload () default {};
}