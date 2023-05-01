package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Update Password Class.
 */
@Controller
public class UpdatePasswordController {
    Logger logger = LoggerFactory.getLogger(UpdatePasswordController.class);

    @Autowired
    UserService userService;

    Optional<User> user;
    User currentUser;

    String currentToken;


    private void checkPasswordsMatchAndIsSecure(UpdatePasswordForm updatePasswordForm, BindingResult bindingResult, String token) {
        /*testUser = userService.findUserByEmail("test@gmail.com");
        currentUser = testUser.get();*/

        user = userService.findByToken(currentToken);
        currentUser = user.get();

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

    /**
     * Gets update password form to be displayed
     * @param model
     * @return
     */
    @GetMapping("/update-password/{token}")
    public String updatePasswordForm(Model model, HttpServletRequest request, @PathVariable String token, RedirectAttributes redirectAttributes) {
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
        model.addAttribute("httpServletRequest",request);

        user = userService.findByToken(token);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("invalidTokenMessage", "Token is invalid or expired.");
            return "redirect:/login";
        }

        currentUser = user.get();
        currentToken = token;
        return "updatePassword";
    }

    /**
     * Updates/resets a user's password upon valid password entered
     * @param password             user's new password
     * @param updatePasswordForm   update password form
     * @param bindingResult        binding result
     * @param model                 model
     * @param httpServletResponse   httpServerletResponse
     * @param request               request
     * @param token                 user's unique token to access their update password page
     * @param redirectAttributes    stores messages to be displayed to user on login page
     * @return
     */
    @PostMapping("/update-password/{token}")
    public String updatePassword(
            @RequestParam("password") String password,
            @Validated UpdatePasswordForm updatePasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse,
            HttpServletRequest request, @PathVariable String token,
            RedirectAttributes redirectAttributes) {

        user = userService.findByToken(currentToken);
        currentUser = user.get();


        checkPasswordsMatchAndIsSecure(updatePasswordForm, bindingResult, token);
        model.addAttribute("httpServletRequest",request);


        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "updatePassword";

        }

        redirectAttributes.addFlashAttribute("passwordUpdatedMessage", "Password updated successfully.");

        currentUser.setPassword(password);
        userService.updateOrAddUser(currentUser);


        return "redirect:/login";
    }



}

