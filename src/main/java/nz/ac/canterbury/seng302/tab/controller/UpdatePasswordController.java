package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.form.UpdatePasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Spring Boot Controller class for the Update Password Class.
 */
@Controller
public class UpdatePasswordController {
    Logger logger = LoggerFactory.getLogger(UpdatePasswordController.class);

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

        //model.addAttribute("email", email);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "updatePassword";
        }

       // model.addAttribute("submitted_form_message", "If your email is registered with our system, you will receive a link to reset your password shortly.");

        logger.info("email submitted");

        return "updatePassword";
    }



}

