package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.validator.CompetitionValidators;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

import java.util.List;

/**
 * Form object provided to the controller which contains the fields for the user to enter and validation for the fields.
 */
public class CreateAndEditCompetitionForm {

    @TeamFormValidators.teamNameValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String name;

    @TeamFormValidators.teamSportValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String sport;

    @CompetitionValidators.competitionGradeLevelValidator
    private String gradeLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}
