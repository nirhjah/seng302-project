package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;

public class ActivityDescriptionCheck implements ConstraintValidator<ActivityFormValidators.descriptionValidator, String> {

    private String regex;

    @Override
    public void initialize(ActivityFormValidators.descriptionValidator constraintAnnotation) {
        this.regex = constraintAnnotation.regexMatch();
    }

    @Override
    public boolean isValid(String description, ConstraintValidatorContext context) {
        if (description == null || description.isEmpty()) {
            return true;
        } else {
            return description.matches(regex);
        }
    }
}
