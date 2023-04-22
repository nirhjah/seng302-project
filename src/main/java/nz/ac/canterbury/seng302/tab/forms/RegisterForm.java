package nz.ac.canterbury.seng302.tab.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static nz.ac.canterbury.seng302.tab.validator.UserFormValidators.INVALID_NAME_MSG;

/**
 * The form provided to the user when registering for the website.
 * 
 * Contains what little validation we can provide using Jakarta defaults.
 * More complex validation is handled inside {@link UserFormValidators}
 */
public class RegisterForm {

    @UserFormValidators.NameValidator
    String firstName;

    @UserFormValidators.NameValidator
    String lastName;

    @UserFormValidators.EmailValidator
    String email;

    @UserFormValidators.addressValidator
    private String addressLine1;

    @UserFormValidators.addressValidator
    private String addressLine2;

    @UserFormValidators.postcodeValidator
    private String postcode;

    @UserFormValidators.countryValidator
    private String country;

    @UserFormValidators.cityValidator

    private String city;

    @UserFormValidators.suburbValidator
    private String suburb;


    @UserFormValidators.DateOfBirthValidator(minimumAge = 13, message = "You must be at least 13 years old")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date dateOfBirth;

    @UserFormValidators.PasswordValidator
    @NotBlank(message = UserFormValidators.NOT_BLANK_MSG)
    String password;

    @NotBlank(message = UserFormValidators.NOT_BLANK_MSG)
    String confirmPassword;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

}
