package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateAndEditTeamForm {

    @TeamFormValidators.teamNameValidator(message = TeamFormValidators.INVALID_TEAM_NAME_MSG)
    private String name;

    @TeamFormValidators.teamSportValidator(message = TeamFormValidators.INVALID_TEAM_SPORT_MSG)
    private String sport;

    @LocationValidators.addressValidator
    private String addressLine1;

    @LocationValidators.addressValidator
    private String addressLine2;

    @LocationValidators.postcodeValidator
    private String postcode;

    @LocationValidators.countryCitySuburbValidator(regexMatch = LocationValidators.VALID_COUNTRY_SUBURB_CITY_REGEX, message = LocationValidators.INVALID_COUNTRY_SUBURB_CITY_MSG)
    private String country;

    @LocationValidators.countryCitySuburbValidator(regexMatch = LocationValidators.VALID_COUNTRY_SUBURB_CITY_REGEX, message = LocationValidators.INVALID_COUNTRY_SUBURB_CITY_MSG)
    private String city;

    @LocationValidators.suburbValidator
    private String suburb;

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
}
