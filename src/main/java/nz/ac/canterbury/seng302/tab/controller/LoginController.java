package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Gets form to be displayed
     * @return thymeleaf register
     */
    @GetMapping("/login")
    public String form(@RequestParam(name="error", required = false, defaultValue = "false") String error,
                       Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest", request);

        model.addAttribute("passwordUpdatedMessage", (String)model.asMap().get("passwordUpdatedMessage"));


        if (error.equals("true"))
        {
            model.addAttribute("errorMessage", "Invalid Email or Password");
        }
        else
        {
            model.addAttribute("errorMessage", "");
        }
        logger.info("GET /login");

        return "login";
    }

    /**
     * Takes the user to the home page if they don't want to login
     * @return redirect to the home page
     */
    @GetMapping("/cancel-login")
    public String cancelLogin()
    {
        logger.info("GET /");
        return "redirect:./demo";
    }
}
