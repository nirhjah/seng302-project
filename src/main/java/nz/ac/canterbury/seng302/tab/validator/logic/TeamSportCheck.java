package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

/**
 * <p>Implementation for the {@link TeamFormValidators} annotation.</p>
 * Checks whether the provided data is not empty and meets the regex.
 */
public class TeamSportCheck implements ConstraintValidator<TeamFormValidators.teamSportValidator, String> {

    private String regex;

    @Override
    public void initialize(TeamFormValidators.teamSportValidator constraintAnnotation) {
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
