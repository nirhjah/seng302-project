package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;

import java.time.LocalDateTime;

public class CreateActivityForm {

    @ActivityFormValidators.activityTypeValidator
    private Activity.ActivityType activityType;

    @ActivityFormValidators.teamSelectionValidator
    private Team team;

    @ActivityFormValidators.descriptionValidator
    private String description;

    @ActivityFormValidators.startActivityValidator
    private LocalDateTime startDateTime;

    @ActivityFormValidators.endActivityValidator
    private LocalDateTime endDateTime;

    public Activity.ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(Activity.ActivityType activityType) {
        this.activityType = activityType;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
