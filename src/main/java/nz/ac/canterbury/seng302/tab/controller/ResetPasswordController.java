package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.ResetPasswordForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Reset Password Class.
 */
@Controller
public class ResetPasswordController {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private Optional<User> user;

    private String currentToken;

    @Autowired
    public ResetPasswordController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Checks if password matches other fields and is secure
     * @param resetPasswordForm   used to get field values of password and confirm password
     * @param bindingResult       stores errors
     * @param token               is used to find the user
     */
    private void checkPasswordsMatchAndIsSecure(ResetPasswordForm resetPasswordForm, BindingResult bindingResult, String token) {

        user = userService.findByToken(currentToken);

        String password = resetPasswordForm.getPassword();
        String confirmPassword = resetPasswordForm.getConfirmPassword();
        // Check #1: Passwords match
        if (password.equals(confirmPassword)) {
            // Check #2: Password doesn't "contain any other field"
            String[] otherFields = new String[]{user.get().getFirstName(), user.get().getLastName(), user.get().getEmail()};
            if(password.length() > 0) {
                for (String field : otherFields) {
                    if (field != "") {
                        if (password.toLowerCase().contains(field.toLowerCase())) {
                            bindingResult.addError(new FieldError("resetPasswordForm", "password", "Password can't contain values from other fields"));
                            break;
                        }
                    }
                }
            }
        }
        else {
            bindingResult.addError(new FieldError("resetPasswordForm", "password", "Passwords do not match"));

        }
    }

    /**
     * Gets the page to show the reset password form
     * @param token user's unique token to get to this page
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @param request request
     * @param redirectAttributes stores error messages to be displayed
     * @return reset password page
     */
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        model.addAttribute("resetPasswordForm", new ResetPasswordForm());
        model.addAttribute("httpServletRequest",request);

        user = userService.findByToken(token);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("invalidTokenMessage", "Token is invalid or expired.");
            return "redirect:/login";
        }

        currentToken = token;
        return "resetPassword";

    }

    /**
     * Resets a user's password upon valid password entered
     * @param password             user's new password
     * @param resetPasswordForm   reset password form
     * @param bindingResult        binding result
     * @param model                 model
     * @param httpServletResponse   httpServerletResponse
     * @param request               request
     * @param redirectAttributes    stores messages to be displayed to user on login page
     * @return login page upon successfully resetting password
     */
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("password") String password,
            @Validated ResetPasswordForm resetPasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        user = userService.findByToken(currentToken);


        checkPasswordsMatchAndIsSecure(resetPasswordForm, bindingResult, currentToken);
        model.addAttribute("httpServletRequest",request);


        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "resetPassword";

        }

        redirectAttributes.addFlashAttribute("passwordUpdatedMessage", "Password updated successfully.");

        userService.updatePassword(user.get(), password);
        return "redirect:/login";
    }



}

