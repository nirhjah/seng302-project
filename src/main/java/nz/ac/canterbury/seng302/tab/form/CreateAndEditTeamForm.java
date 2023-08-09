package nz.ac.canterbury.seng302.tab.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateAndEditTeamForm {

    @TeamFormValidators.teamNameValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG_TEAM_NAME)
    private String name;

    @TeamFormValidators.teamSportValidator(message = TeamFormValidators.INVALID_SPORT_MSG)
    private String sport;
    @LocationValidators.addressValidator
    private String addressLine1;
    @LocationValidators.addressValidator
    private String addressLine2;
    @LocationValidators.postcodeValidator
    private String postcode;
    @LocationValidators.countryCitySuburbValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String country;

    @LocationValidators.countryCitySuburbValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String city;

    @LocationValidators.suburbValidator
    private String suburb;

    @NotNull
    private Grade.Age age;
    
    @NotNull
    private Grade.Sex sex;
    
    @NotNull
    private Grade.Competitiveness competitiveness;

    /**
     * Fills out all of this form's fields <strong>in-place</strong>.
     * @param team The team we'll be populating this with
     */
    public void prepopulate(Team team) {
        Location location = team.getLocation();
        this.name = team.getName();
        this.sport = team.getSport();
        if (team.getGrade() != null) {  // Legacy accounts might not have a grade
            this.age = team.getGrade().getAge();
            this.sex = team.getGrade().getSex();
            this.competitiveness = team.getGrade().getCompetitiveness();
        }
        this.addressLine1 = location.getAddressLine1();
        this.addressLine2 = location.getAddressLine2();
        this.postcode = location.getPostcode();
        this.country = location.getCountry();
        this.city = location.getCity();
        this.suburb = location.getSuburb();

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

    public Grade.Age getAge() {
        return age;
    }

    public void setAge(Grade.Age age) {
        this.age = age;
    }

    public Grade.Sex getSex() {
        return sex;
    }

    public void setSex(Grade.Sex sex) {
        this.sex = sex;
    }

    public Grade.Competitiveness getCompetitiveness() {
        return competitiveness;
    }

    public void setCompetitiveness(Grade.Competitiveness competitiveness) {
        this.competitiveness = competitiveness;
    }

    /**
     * Creates a Grade object from this form's age, sex, and competitiveness values.
     */
    public Grade makeGrade() {
        return new Grade(age, sex, competitiveness);
    }

}
