package nz.ac.canterbury.seng302.tab.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateActivityForm {

    @ActivityFormValidators.activityTypeValidator
    private ActivityType activityType;

    private long teamId;
    
    private long formationId;

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


    private String playerAndPositions;

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    
    public long getFormation() {
        return formationId;
    }

    public void setFormation(long formation) {
        this.formationId = formation;
    }

    public long getTeam() {
        return teamId;
    }

    public void setTeam(long team) {
        this.teamId = team;
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

}
