package tech.shooting.ipsc.validator;

import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {
	String message () default "{Incorrect password}";

	Class<?>[] groups () default {};

	Class<? extends Payload>[] payload () default {};
	// String[] patterns();
}