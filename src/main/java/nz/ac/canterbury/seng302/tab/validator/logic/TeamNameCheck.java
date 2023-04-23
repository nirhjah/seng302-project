package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;


/**
 * <p>Implementation for the {@link TeamFormValidators} annotation.</p>
 * Checks whether the provided data is not empty and meets the regex.
 */
public class TeamNameCheck implements ConstraintValidator<TeamFormValidators.teamNameValidator, String> {

    private String regex;

    @Override
    public void initialize(TeamFormValidators.teamNameValidator constraintAnnotation) {
        this.regex = constraintAnnotation.regexMatch();
    }

    @Override
    public boolean isValid(String teamName, ConstraintValidatorContext context) {
        if (teamName == null || teamName.isEmpty()) {
            return true;
        } else {
            return teamName.matches(regex);
        }
    }
}
