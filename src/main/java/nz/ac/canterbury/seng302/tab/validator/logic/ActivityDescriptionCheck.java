package nz.ac.canterbury.seng302.tab.validator.logic;

import java.util.regex.Pattern;

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
            // Descriptions are allowed to be multi-line, and by default
            // the "(.*)" pattern doesn't match on newlines. So,
            // we have to add a DOTALL :(.
            // Seriously we should just use the Pattern annotation
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

            return pattern.matcher(description).matches();
        }
    }
}
