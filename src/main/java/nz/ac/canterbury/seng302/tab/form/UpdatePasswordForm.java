package nz.ac.canterbury.seng302.tab.form;

import jakarta.validation.constraints.NotBlank;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

public class UpdatePasswordForm {

    @UserFormValidators.PasswordValidator
    @NotBlank(message = UserFormValidators.NOT_BLANK_MSG)
    String password;

    @NotBlank(message = UserFormValidators.NOT_BLANK_MSG)
    String confirmPassword;

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

}
