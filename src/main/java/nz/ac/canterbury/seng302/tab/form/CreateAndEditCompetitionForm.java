package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Form object provided to the controller which contains the fields for the user to enter and validation for the fields.
 */
public class CreateAndEditCompetitionForm {

    @TeamFormValidators.teamNameValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String name;

    @TeamFormValidators.teamSportValidator(message = TeamFormValidators.INVALID_CHARACTERS_MSG)
    private String sport;

    @LocationValidators.addressValidator
    private String addressLine1;

    @LocationValidators.addressValidator
    private String addressLine2;

    @LocationValidators.postcodeValidator
    private String postcode;

    @LocationValidators.suburbValidator
    private String suburb;

    @LocationValidators.suburbValidator
    private String country;

    @LocationValidators.suburbValidator
    private String city;

    private Grade.Age age;

    private Grade.Sex sex;

    private Grade.Competitiveness competitiveness;

    private Set<Team> teams;

    private Set<User> players;

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

    public String getAddressLine1() { return addressLine1; }

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

    public Grade getGrade() {
        return new Grade(this.age, this.sex, this.competitiveness);
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public Set<?> getCompetitors() {
        if (teams != null) {
            return teams;
        } else if (players != null){
            return players;
        } else {
            return new HashSet<>();
        }
    }

}
