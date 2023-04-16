package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateAndEditTeamForm {

    private String sport;

    private String name;

    @TeamFormValidators.addressValidator
    private String addressLine1;

    @TeamFormValidators.addressValidator
    private String addressLine2;

    @TeamFormValidators.postcodeValidator
    private String postcode;

    @TeamFormValidators.countryCitySuburbValidator(regexMatch = "^\\p{L}+[\\- '\\p{L}]*$", message = "May include letters, hyphens, apostrophes and spaces. Must start with letter")
    private String country;

    @TeamFormValidators.countryCitySuburbValidator(regexMatch = "^\\p{L}+[\\- '\\p{L}]*$", message = "May include letters, hyphens, apostrophes and spaces. Must start with letter")
    private String city;

    @TeamFormValidators.suburbValidator
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
}
