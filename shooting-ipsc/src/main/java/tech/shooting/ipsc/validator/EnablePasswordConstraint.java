package tech.shooting.ipsc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface EnablePasswordConstraint {

	String message () default ValidationConstants.USER_OLD_PASSWORD_MATCH_MESSAGE;

	Class<?>[] groups () default {};

	Class<? extends Payload>[] payload () default {};
}