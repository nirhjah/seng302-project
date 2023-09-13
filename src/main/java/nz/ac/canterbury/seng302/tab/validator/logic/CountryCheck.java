package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether the provided date country is valid.
 */
public class CountryCheck implements ConstraintValidator<UserFormValidators.countryValidator, String> {

    @Override
    public void initialize(UserFormValidators.countryValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        if (country.equals("")) {
            return true;
        }
        else {
            return (country.matches(UserFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX) && country.length() <= 30);
        }
    }
}