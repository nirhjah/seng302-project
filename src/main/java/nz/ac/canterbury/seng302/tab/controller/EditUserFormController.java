package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.EditUserForm;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class EditUserFormController {

    @Autowired
    UserService userService;

    static final String TEMPLATE_NAME = "undefined";

    @GetMapping("/edit-user")
    public String getEditUserForm(
            // TODO: Remove the `id` field, and just grab the current user's details once
            // auth's back up.
            // //@RequestParam(name = "id", required = true) long id,
            EditUserForm editUserForm) {
        // User user = userService.findUserById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = User.defaultDummyUser();
        editUserForm.prepopulate(user);

        return TEMPLATE_NAME;
    }

    @PostMapping("/edit-user")
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
