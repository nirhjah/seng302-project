package nz.ac.canterbury.seng302.tab.form;

import java.util.Date;
import java.util.List;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;

public class EditUserForm {
    @UserFormValidators.NameValidator
    private String firstName;

    @UserFormValidators.NameValidator
    private String lastName;

    @UserFormValidators.EmailValidator
    private String email;

    @UserFormValidators.DateOfBirthValidator(minimumAge = 13, message = "You must be at least 13 years old")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @LocationValidators.addressValidator
    private String addressLine1;

    @LocationValidators.addressValidator
    private String addressLine2;

    @LocationValidators.postcodeValidator
    private String postcode;

    @LocationValidators.countryCityValidator
    private String country;

    @LocationValidators.countryCityValidator
    private String city;

    @LocationValidators.suburbValidator
    private String suburb;

    private List<Sport> favouriteSports;

    /**
     * Fills out all of the <em>empty</em> fields with the user's details.
     * @param user The user we'll be populating this with.
     */
    public void prepopulate(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.dateOfBirth = user.getDateOfBirth();
        this.addressLine1 = user.getLocation().getAddressLine1();
        this.addressLine2 = user.getLocation().getAddressLine2();
        this.suburb = user.getLocation().getSuburb();
        this.postcode = user.getLocation().getPostcode();
        this.city = user.getLocation().getCity();
        this.country = user.getLocation().getCountry();
        this.favouriteSports = user.getFavoriteSports();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public List<Sport> getFavouriteSports() { return favouriteSports; }

    public void setFavouriteSports(List<Sport> sports) {this.favouriteSports = sports; }

}
