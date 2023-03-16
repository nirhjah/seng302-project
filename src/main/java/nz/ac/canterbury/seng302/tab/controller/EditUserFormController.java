package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.form.EditUserForm;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class EditUserFormController {
    
    @Autowired
    UserService userService;

    static final String TEMPLATE_NAME = "undefined";

    @GetMapping("/edit-user")
    public String editUserForm(
        @RequestParam(name = "id", required=true) long id,
        @Valid EditUserForm editUserForm,
        BindingResult bindingResult) {
            return TEMPLATE_NAME;
        }
}
