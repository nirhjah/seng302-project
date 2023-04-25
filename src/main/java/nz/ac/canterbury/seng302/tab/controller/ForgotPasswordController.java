package nz.ac.canterbury.seng302.tab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Spring Boot Controller class for the Forgot Password Class.
 */
@Controller
public class ForgotPasswordController {
    Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);


    @GetMapping("/forgot-password")
    public String forgotPasswordForm(@RequestParam(name="error", required = false, defaultValue = "false") String error,
                         Model model)
    {

        if (error.equals("true"))
        {
            model.addAttribute("errorMessage", "Invalid Email");
        }
        else
        {
            model.addAttribute("errorMessage", "");
        }

        return "forgotPassword";
    }
}

