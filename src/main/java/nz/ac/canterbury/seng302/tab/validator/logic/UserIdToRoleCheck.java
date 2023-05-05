package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

import java.util.Map;



/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether city is valid
 */
public class UserIdToRoleCheck implements ConstraintValidator<UserFormValidators.cityValidator, Map<String, String>> {

    /**
     * List of Valid roles
     */
    @Override
    public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
        /*
        If all string keys are a valid role name, then it's OK.
         */
        return value.keySet().stream().allMatch(Role::isValidRole);
    }
}
