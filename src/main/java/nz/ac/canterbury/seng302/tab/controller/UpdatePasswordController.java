package nz.ac.canterbury.seng302.tab.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.UpdatePasswordForm;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * User Story U22 - Update Password
 * 
 * Gives the user a simple "[old][new][retype] password" field, and
 * updates their password accordingly
 */
@Controller
public class UpdatePasswordController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Fills the model with globally required fields for navBar.html
     * (Wish every controller didn't need to define these...)
     * 
     * @param model   Values added to this
     * @param user    We need the first&last name + profile pic
     * @param request New Thymeleaf deprecated ${#request} sooo...
     */
    private void prefillModel(Model model, User user, HttpServletRequest request) {
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
    }

    /**
     * <p>
     * Checks that the update password form is correct.
     * </p>
     * All errors are added to the BindingResult, so they're added
     * alongside the annotation validation errors like <code>@NotBlank</code>.
     * <ul>
     * <li>U22 - AC2: The 'old password' field must match the user's current
     * password</li>
     * <li>U22 - AC3: The 'old' and 'retype' password fields must match</li>
     * <li>U22 - AC4: The password must be strong <em>(Not handled here)</em></li>
     * </ul>
     * 
     * @param bindingResult      The Jakarta error object (mutable)
     * @param updatePasswordForm The form we're reading from
     * @param hashedPassword     The current password of the current user
     */
    private void validateForm(BindingResult bindingResult, UpdatePasswordForm updatePasswordForm,
            String hashedPassword) {
        final String FORM_NAME = "updatePasswordForm";

        // U22 - AC2: Is the old password correct?
        if (!passwordEncoder.matches(updatePasswordForm.getOldPassword(), hashedPassword)) {
            bindingResult.addError(new FieldError(FORM_NAME, "oldPassword", "The old password is incorrect."));
        }
        // U22 - AC3: Is 'retype password' the same?
        if (!updatePasswordForm.getNewPassword().equals(updatePasswordForm.getConfirmPassword())) {
            bindingResult.addError(new FieldError(FORM_NAME, "confirmPassword", "Passwords do not match."));
        }
        // U22 - AC4 is already handled by the UpdatePasswordForm's password annotation.
    }

    /**
     * Gives the user their form.
     * 
     * This is a simple function, no pre-population is required.
     * @param updatePasswordForm A blank form for the user to fill
     * @param request Required in navBar.html 
     */
    @GetMapping("/updatePassword")
    public String getUpdatePassword(
            UpdatePasswordForm updatePasswordForm,
            Model model,
            HttpServletRequest request) {
        logger.info("POST /updatePassword");

        // Get the currently logged in user
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return "redirect:login";
        }
        User user = currentUser.get();
        prefillModel(model, user, request);
        return "updatePassword";
    }

    /**
     * Receives the form from the user.
     * 
     * If the form is valid, the user's password changes, and an email is sent.
     * 
     * @param updatePasswordForm The filled out form
     * @param bindingResult Contains any form validation errors (incorrect password, etc.)
     */
    @PostMapping("/updatePassword")
    public String submitUpdatePassword(
            @Valid UpdatePasswordForm updatePasswordForm,
            BindingResult bindingResult,
            Model model, HttpServletRequest request) {
        logger.info("GET /updatePassword");

        // Get the currently logged in user
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return "redirect:login";
        }
        User user = currentUser.get();
        prefillModel(model, user, request);
        // Check that the form is valid
        validateForm(bindingResult, updatePasswordForm, user.getPassword());
        if (bindingResult.hasErrors()) {
            return "updatePassword";
        } else {
            String hashedPassword = passwordEncoder.encode(updatePasswordForm.getNewPassword());
            user.setPassword(hashedPassword);
            userService.updateOrAddUser(user);
            userService.updatePassword(user);
            return "redirect:user-info/self";
        }
    }
}
