package nz.ac.canterbury.seng302.tab.controller;

import java.util.regex.Pattern;

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
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.tab.authentication.ContinueEntryPoint;
import nz.ac.canterbury.seng302.tab.authentication.SecurityConfiguration;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    private UserService userService;

    /** <p>To protect against Open Redirect Vulnerabilities, the URL must start with a single slash.</p>
     * Pattern created by <a href="https://stackoverflow.com/a/34091221">Rob Winch</a>
     */
    private static final Pattern LOCAL_URL_PATTERN = Pattern.compile("^/([^/].*)?$");

    private static final String DEFAULT_REDIRECT = ContinueEntryPoint.LOGIN_REDIRECT_URL_PARAM;

    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String home() {
        logger.info("GET /homeForm");
        return "redirect:./home";
    }
    /**
     * Gets form to be displayed
     * @return thymeleaf register
     * @throws AuthenticationException If logging in failed with an unknown error
     */
    @GetMapping("/home")
    public String form(
            @RequestParam(name="error", required=false) String error,
            @RequestParam(name=DEFAULT_REDIRECT, required=false) String redirectUrl,
            Model model,
            HttpServletRequest request,
            HttpSession session) {
        model.addAttribute("httpServletRequest", request);
        logger.info("GET /home");
        
        // If the redirect URL isn't local (Open Redirect Vulnerability), trash it.
        if (redirectUrl != null && !LOCAL_URL_PATTERN.matcher(redirectUrl).matches()) {
            session.removeAttribute(DEFAULT_REDIRECT);
            return "redirect:login";
        }

        // If you're already logged in, just redirect
        if (userService.getCurrentUser().isPresent()) {
            return "redirect:" + SecurityConfiguration.DEFAULT_LOGIN_REDIRECT_URL;
        }

        boolean hasError = (error != null);

        /*
         * We have to store the "Success URL" in the session variables, because Spring forces
         * a redirect to a constant URL on login failure (/login?error), so normal parameters are forgotten.
         * Logic:
         * - /login: Don't redirect
         * - /login?continue=/here: Save the URL, and redirect on success
         * - /login?error: The redirect info's gone, so fetch the one saved above
         * - /login?error&continue=/here: IMPOSSIBLE STATE
         */ 
        if (hasError) {
            redirectUrl = (String) session.getAttribute(DEFAULT_REDIRECT);
        }

        if (hasError) {
            String errorMessage;
            Exception exception = (Exception)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

            if (exception instanceof BadCredentialsException) {
                errorMessage = "Invalid Email or Password";
            } else if (exception instanceof DisabledException) {
                errorMessage = "Please verify your email before trying to log in";
            } else {
                // We don't know what type of error this is.
                // So make a fuss and throw it.
                throw new IllegalArgumentException(
                        "Unknown exception inside the session's `WebAttributes.AUTHENTICATION_EXCEPTION`",
                        exception);
            }
            model.addAttribute("errorMessage", errorMessage);
        }

        model.addAttribute("passwordUpdatedMessage", model.asMap().get("passwordUpdatedMessage"));
        model.addAttribute("invalidTokenMessage", model.asMap().get("invalidTokenMessage"));
        
        model.addAttribute(DEFAULT_REDIRECT, redirectUrl);
        // Maintain the redirect URL in case of error
        session.setAttribute(DEFAULT_REDIRECT, redirectUrl);

        if (!model.containsAttribute("message")) {
            model.addAttribute("message", session.getAttribute("message"));
        }

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
