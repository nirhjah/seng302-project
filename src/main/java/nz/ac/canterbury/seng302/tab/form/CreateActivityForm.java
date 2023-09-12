package nz.ac.canterbury.seng302.tab.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateActivityForm {

    @ActivityFormValidators.activityTypeValidator
    private ActivityType activityType;

    private long team;
    
    private long formation;

    @ActivityFormValidators.descriptionValidator
    private String description;

    @ActivityFormValidators.startActivityValidator
    private LocalDateTime startDateTime;

    @ActivityFormValidators.endActivityValidator
    private LocalDateTime endDateTime;

    @NotBlank
    @LocationValidators.addressValidator
    private String addressLine1;

    @LocationValidators.addressValidator
    private String addressLine2;

    @NotBlank
    @LocationValidators.postcodeValidator
    private String postcode;

    @LocationValidators.countryCitySuburbValidator(regexMatch = TeamFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX, message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String country;

    @LocationValidators.countryCitySuburbValidator(regexMatch = TeamFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX, message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String city;

    @LocationValidators.suburbValidator
    private String suburb;

    public String getLineup() {
        return lineup;
    }

    public void setLineup(String lineup) {
        this.lineup = lineup;
    }

    private String lineup;

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    
    public long getFormation() {
        return formation;
    }

    public void setFormation(long formation) {
        this.formation = formation;
    }

    public long getTeam() {
        return team;
    }

    public void setTeam(long team) {
        this.team = team;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Sets the description field (called by Spring when filling the form)
     */
    public void setDescription(String description) {
        // We strip this so the description isn't just "\n\n\n\nHELLO\n\n"
        String newDescription = description.strip();
        // Bug: On Windows newlines are "\r\n", on Linux (and in browsers) they're "\n".
        // So a newline heavy description can fail the length test on Windows.
        // Fix: Strip out the "\r"
        newDescription = newDescription.replace("\r\n", "\n");
        this.description = newDescription;
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public Location getLocation() {
        return new Location(this.addressLine1, this.addressLine2, this.suburb, this.city, this.postcode, this.country);
    }

    public void prepopulate(Activity activity) {
        Location location = activity.getLocation();
        this.description = activity.getDescription();
        this.startDateTime = activity.getActivityStart();
        this.endDateTime = activity.getActivityEnd();
        this.addressLine1 = location.getAddressLine1();
        this.addressLine2 = location.getAddressLine2();
        this.postcode = location.getPostcode();
        this.country = location.getCountry();
        this.city = location.getCity();
        this.suburb = location.getSuburb();
        this.activityType = activity.getActivityType();
        
        Team teamOrNull = activity.getTeam();
        if (teamOrNull != null) {
            this.team = teamOrNull.getTeamId();
        } else {
            this.team = -1;
        }

    }


}
