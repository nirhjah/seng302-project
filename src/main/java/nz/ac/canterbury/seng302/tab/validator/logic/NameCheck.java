package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether the provided name is valid.
 */
public class NameCheck implements ConstraintValidator<UserFormValidators.NameValidator, String> {

    @Override
    public void initialize(UserFormValidators.NameValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == "") {
            return true;
        }
        else {
            return name.matches(UserFormValidators.VALID_NAME_REGEX) && name.length() <= 100;
        }
    }
}