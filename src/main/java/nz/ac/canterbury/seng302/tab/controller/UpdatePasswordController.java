package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.UpdatePasswordForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Update Password Class.
 */
@Controller
public class UpdatePasswordController {
    Logger logger = LoggerFactory.getLogger(UpdatePasswordController.class);

    @Autowired
    UserService userService;


    private void checkPasswordsMatchAndIsSecure(UpdatePasswordForm updatePasswordForm, BindingResult bindingResult) {
        Optional<User> testUser = userService.findUserByEmail("test@gmail.com");
        User currentUser = testUser.get();

        String password = updatePasswordForm.getPassword();
        String confirmPassword = updatePasswordForm.getConfirmPassword();
        // Check #1: Passwords match
        if (password.equals(confirmPassword)) {
            // Check #2: Password doesn't "contain any other field"
            String[] otherFields = new String[]{currentUser.getFirstName(), currentUser.getLastName(), currentUser.getEmail()};
            if(password.length() > 0) {
                for (String field : otherFields) {
                    if (field != "") {
                        if (password.toLowerCase().contains(field.toLowerCase())) {
                            bindingResult.addError(new FieldError("updatePasswordForm", "password", "Password can't contain values from other fields"));
                            break;
                        }
                    }
                }
            }
        }
        else {
            bindingResult.addError(new FieldError("updatePasswordForm", "password", "Passwords do not match"));

        }
    }

    @GetMapping("/update-password")
    public String updatePasswordForm(Model model) {
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
        return "updatePassword";
    }

    @PostMapping("/update-password")
    public String submitEmail(
            @Validated UpdatePasswordForm updatePasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse
    ) {

        checkPasswordsMatchAndIsSecure(updatePasswordForm, bindingResult);
        
        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "updatePassword";
        }



        return "updatePassword";
    }



}

