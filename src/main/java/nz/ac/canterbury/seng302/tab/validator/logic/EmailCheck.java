package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether the provided email is well formed.
 */
public class EmailCheck implements ConstraintValidator<UserFormValidators.EmailValidator, String> {

    @Override
    public void initialize(UserFormValidators.EmailValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == "") {
            return true;
        }
        else {
            return (email.matches(UserFormValidators.VALID_EMAIL_REGEX) && email.length() < 100);
        }
    }
}