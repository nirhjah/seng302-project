package nz.ac.canterbury.seng302.tab.form;

import java.time.LocalDate;

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
    private LocalDate dateOfBirth;

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
