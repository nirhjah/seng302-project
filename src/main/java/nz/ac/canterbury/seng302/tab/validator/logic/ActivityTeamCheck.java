package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;

public class ActivityTeamCheck implements ConstraintValidator<ActivityFormValidators.teamSelectionValidator, Team> {

    @Override
    public void initialize(ActivityFormValidators.teamSelectionValidator constraintAnnotation) {}

    @Override
    public boolean isValid(Team team, ConstraintValidatorContext context) {return true;}
}
