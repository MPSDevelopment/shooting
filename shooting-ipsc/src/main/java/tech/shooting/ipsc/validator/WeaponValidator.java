package tech.shooting.ipsc.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WeaponValidator implements ConstraintValidator<ValidateTypeWeapon,String> {


    @Override
    public void initialize (ValidateTypeWeapon constraintAnnotation) {

    }

    @Override
    public boolean isValid (String s, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
