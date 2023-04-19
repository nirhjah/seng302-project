package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link TeamFormValidators} annotation.</p>
 * Checks whether the provided data is not empty and meets the regex.
 */
public class CountryCitySuburbCheck implements ConstraintValidator<TeamFormValidators.countryCitySuburbValidator, String> {

    private String regex;

    @Override
    public void initialize(TeamFormValidators.countryCitySuburbValidator constraintAnnotation) {
        this.regex = constraintAnnotation.regexMatch();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        } else {
            return value.matches(regex);
        }
    }
}
