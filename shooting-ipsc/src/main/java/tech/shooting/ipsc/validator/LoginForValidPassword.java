package tech.shooting.ipsc.validator;

import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginForValidPassword {

    String message () default "{Incorrect login}";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

    // String[] patterns();

}