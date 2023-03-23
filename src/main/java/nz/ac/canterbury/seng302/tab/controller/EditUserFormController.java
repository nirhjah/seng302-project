package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
        Sport s1 = new Sport("Hockey");
        Sport s2 = new Sport("Football");
        List<Sport> sports = new ArrayList<>();
        sports.add(s1);
        sports.add(s2);
        User u = user.get();
        u.setFavoriteSports(sports);
        userService.updateOrAddUser(u);
        editUserForm.prepopulate(u);
        model.addAttribute("favouriteSports", u.getFavoriteSports());
        return "editUserForm";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(
            @Valid EditUserForm editUserForm,
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
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "editUserForm";
        }

        // Log-out if the user changes their email
        boolean shouldLogout = !user.getEmail().equals(editUserForm.getEmail());

        user.setFirstName(editUserForm.getFirstName());
        user.setLastName(editUserForm.getLastName());
        user.setEmail(editUserForm.getEmail());
        user.setDateOfBirth(editUserForm.getDateOfBirth());
        user.setFavoriteSports(editUserForm.getFavouriteSports());
        userService.updateOrAddUser(user);

        if (shouldLogout) {
            httpServletRequest.logout();
            return "redirect:login";
        }

        return "redirect:user-info/self";
    }
}
