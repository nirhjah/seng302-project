package nz.ac.canterbury.seng302.tab.forms;

import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date dateOfBirth;

    @NotBlank
    @Size(min=8, max=100)
    String password;

    @NotBlank
    String confirmPassword;

    @NotBlank
    String addressLine1;
    @NotBlank
    String addressLine2;
    @NotBlank
    String suburb;
    @NotBlank
    @Pattern(regexp = "^\\d\\d\\d\\d$")
    String postcode;
    @NotBlank
    String city;
    @NotBlank
    String country;

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

    public String getAddressLine1() {return addressLine1;}
    public void setAddressLine1(String al1) {addressLine1 = al1;}

    public String getAddressLine2() {return addressLine2;}
    public void setAddressLine2(String al2) {addressLine2 = al2;}

    public void setSuburb(String sub) {suburb = sub;}
    public String getSuburb() {return suburb;}

    public void setPostcode(String pcode) {postcode = pcode;}
    public String getPostcode() {return postcode;}

    public void setCity(String city) {this.city = city;}
    public String getCity() {return city;}

    public void setCountry(String country) {this.country = country;}
    public String getCountry() {return country;}
}
