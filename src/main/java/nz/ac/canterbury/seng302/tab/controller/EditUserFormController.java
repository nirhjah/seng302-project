package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.EditUserForm;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class EditUserFormController {

    @Autowired
    UserService userService;

    static final String TEMPLATE_NAME = "undefined";

    @GetMapping("/editUser")
    public String getEditUserForm(
            // TODO: Remove the `id` field, and just grab the current user's details once
            // auth's back up.
            // //@RequestParam(name = "id", required = true) long id,
            EditUserForm editUserForm, Model model) {
        // // User user = userService.findUserById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = User.defaultDummyUser();
        editUserForm.prepopulate(user);
        model.addAttribute("user", user);
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("dateOfBirth", user.getDateOfBirthFormatted());
        return "editUserForm";
    }

    @PostMapping("/editUser")
    @ResponseBody
    public String submitEditUserForm(
            // TODO: Remove the `id` field, and just grab the current user's details once
            // auth's back up.
            // //@RequestParam(name = "id", required = true) long id,
            @Valid EditUserForm editUserForm,
            BindingResult bindingResult) {
        ////User user = userService.findUserById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = User.defaultDummyUser();

        // Manual email uniqueness check
        if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
            bindingResult.addError(new FieldError("editUserForm", "email", "Email is already in use."));
        }

        if (bindingResult.hasErrors()) {
            return bindingResult.toString();
        }
        return "All valid!";
    }
}
