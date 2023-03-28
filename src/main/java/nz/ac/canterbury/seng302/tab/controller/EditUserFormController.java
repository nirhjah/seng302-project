package nz.ac.canterbury.seng302.tab.controller;

import java.util.Optional;

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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.EditUserForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;

@Controller
public class EditUserFormController {

    private final Logger logger = LoggerFactory.getLogger(EditUserFormController.class);

    @Autowired
    UserService userService;

    private void prefillModel(Model model) {
        model.addAttribute("validNameRegex", UserFormValidators.VALID_NAME_REGEX);
        model.addAttribute("validNameMessage", UserFormValidators.INVALID_NAME_MSG);
        model.addAttribute("postcodeRegex",UserFormValidators.VALID_POSTCODE_REGEX);
        model.addAttribute("postcodeRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("addressRegex",UserFormValidators.VALID_ADDRESS_REGEX);
        model.addAttribute("addressRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("countryCitySuburbNameRegex",UserFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg",UserFormValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);

    }

    @GetMapping("/editUser")
    public String getEditUserForm(
            EditUserForm editUserForm,
            Model model) {
        prefillModel(model);
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        editUserForm.prepopulate(user.get());

        return "editUserForm";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(
            @Validated EditUserForm editUserForm,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Model model) throws ServletException {

        prefillModel(model);
        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();

        // Manual email uniqueness check
        if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
            bindingResult.addError(new FieldError("editUserForm", "email", "Email is already in use."));
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "editUserForm";
        }

        // Log-out if the user changes their email
        boolean shouldLogout = !user.getEmail().equals(editUserForm.getEmail());

        user.setFirstName(editUserForm.getFirstName());
        user.setLastName(editUserForm.getLastName());
        user.setEmail(editUserForm.getEmail());
        user.setDateOfBirth(editUserForm.getDateOfBirth());
        user.setLocation(editUserForm.getLocation());
        userService.updateOrAddUser(user);

        if (shouldLogout) {
            httpServletRequest.logout();
            return "redirect:login";
        }

        return "redirect:user-info/self";
    }
}
