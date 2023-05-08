package nz.ac.canterbury.seng302.tab.form;

import jakarta.validation.constraints.NotBlank;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

public class UpdatePasswordForm {

    @NotBlank
    private String oldPassword;
    
    @UserFormValidators.PasswordValidator
    private String newPassword;

    @NotBlank
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
