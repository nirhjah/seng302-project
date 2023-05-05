package nz.ac.canterbury.seng302.tab.controller;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Gets form to be displayed
     * @return thymeleaf register
     * @throws AuthenticationException If logging in failed with an unknown error
     */
    @GetMapping("/login")
    public String form(@RequestParam(name="error", required=false, defaultValue="false") String error,
                       Model model, HttpServletRequest request) throws Exception {
        model.addAttribute("httpServletRequest", request);

        if (error.equals("true")) {
            String errorMessage = "";
            Exception exception = (Exception)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

            if (exception instanceof BadCredentialsException) {
                errorMessage = "Invalid Email or Password";
            } else if (exception instanceof DisabledException) {
                errorMessage = "Account is not confirmed.";
            } else {
                // We don't know what type of error this is.
                // So make a fuss and throw it.
                throw new IllegalArgumentException(
                        "Unknown exception inside the session's `WebAttributes.AUTHENTICATION_EXCEPTION`",
                        exception);
            }
            model.addAttribute("errorMessage", errorMessage);
        } else {
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
