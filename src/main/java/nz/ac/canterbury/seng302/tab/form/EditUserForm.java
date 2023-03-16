package nz.ac.canterbury.seng302.tab.form;

import java.util.Date;

import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

public class EditUserForm {
    @UserFormValidators.NameValidator
    private String firstName;

    @UserFormValidators.NameValidator
    private String lastName;

    @UserFormValidators.EmailValidator
    private String email;

    @UserFormValidators.DateOfBirthValidator(minimumAge = 13, message = "You must be at least 13 years old")
    private Date dateOfBirth;

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

}
