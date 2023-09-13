package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether city is valid
 */
public class CityCheck implements ConstraintValidator<UserFormValidators.cityValidator, String> {

    @Override
    public void initialize(UserFormValidators.cityValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String city, ConstraintValidatorContext context) {
        if (city.equals("")) {
            return true;
        }
        else {
            return (city.matches(UserFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX) && city.length() <= 30);
        }
    }
}