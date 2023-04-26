package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.form.ForgotPasswordForm;
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
 * Spring Boot Controller class for the Forgot Password Class.
 */
@Controller
public class ForgotPasswordController {
    Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String submitEmail(
            @RequestParam("email") String email,
            @Validated ForgotPasswordForm forgotPasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse
    ) {

        model.addAttribute("email", email);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "forgotPassword";
        }

        model.addAttribute("submitted_form_message", "If your email is registered with our system, you will receive a link to reset your password shortly.");

        logger.info("email submitted");

        return "forgotPassword";
    }



}

