package nz.ac.canterbury.seng302.tab.form;

import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.springframework.format.annotation.DateTimeFormat;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

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

    private List<Sport> favouriteSports;

    /**
     * Fills out all of the <em>empty</em> fields with the user's details.
     * @param user The user we'll be populating this with.
     */
    public void prepopulate(User user) {
        if (this.firstName == null)
            this.firstName = user.getFirstName();

        if (this.lastName == null)
            this.lastName = user.getLastName();

        if (this.email == null)
            this.email = user.getEmail();

        if (this.dateOfBirth == null)
            this.dateOfBirth = user.getDateOfBirth();

        if (this.favouriteSports == null)
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

    public List<Sport> getFavouriteSports() { return favouriteSports; }

    public void setFavouriteSports(List<Sport> sports) {this.favouriteSports = sports; }

}
