package nz.ac.canterbury.seng302.tab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("email = {}", email);

        User user = userService.findUserByEmail(email).get();
        editUserForm.prepopulate(user);

        return "editUserForm";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(
            @Valid EditUserForm editUserForm,
            BindingResult bindingResult,
            Model model) {
        prefillModel(model);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email).get();

        // Manual email uniqueness check
        if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
            bindingResult.addError(new FieldError("editUserForm", "email", "Email is already in use."));
        }

        if (bindingResult.hasErrors()) {
            return "editUserForm";
        }
        return "redirect:user-info/self";
    }
}
