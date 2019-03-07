package tech.shooting.ipsc.validator;



import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateTypeWeapon {
    String[] acceptedValues();

    String message() default "{Incorrect type of weapon}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
