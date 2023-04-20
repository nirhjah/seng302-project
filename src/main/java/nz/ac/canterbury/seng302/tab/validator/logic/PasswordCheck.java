package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import org.springframework.validation.FieldError;


/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks password meets all requirements
 */
public class PasswordCheck implements ConstraintValidator<UserFormValidators.PasswordValidator, String> {

    @Override
    public void initialize(UserFormValidators.PasswordValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == "") {
            return true;
        }
        else {

            boolean uppercase = false;
            boolean lowercase = false;
            boolean number = false;
            boolean symbol = false;
            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) {
                    number = true;
                } else if (Character.isUpperCase(c)) {
                    uppercase = true;
                } else if (Character.isLowerCase(c)) {
                    lowercase = true;
                } else {
                    symbol = true;
                }
            }

            return ( !(!uppercase || !lowercase || !number || !symbol)  && !(password.length() <= 8 || password.length() >= 100));

        }
    }
}