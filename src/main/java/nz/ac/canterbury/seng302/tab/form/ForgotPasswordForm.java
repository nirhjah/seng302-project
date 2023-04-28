package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

public class ForgotPasswordForm {

    @UserFormValidators.EmailValidator
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
