package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class RegisterController {
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Countries and cities can have letters from all alphabets, with hyphens and
     * spaces. Must start with an alphabetical character
     */
    private final String countryCitySuburbNameRegex = "^\\p{L}+[\\- \\p{L}]*$";

    /** Addresses can have letters, numbers, spaces, commas, periods, hyphens, forward slashes and pound signs. Must
     * include at least one alphanumeric character **/
    private  final String addressRegex = "^[\\p{L}\\p{N}]+[\\- ,./#\\p{L}\\p{N}]*$";

    /** Allow letters, numbers, forward slashes and hyphens. Must start with an alphanumeric character. */
    private final String postcodeRegex = "^[\\p{L}\\p{N}]+[\\-/\\p{L}\\p{N}]*$";

    /**
     * Logs the given user in.
     * Because our logins are entirely handled through the security chain, we have
     * to hack together
     * a login if we can't go through it.
     * 
     * @param user The user who'll be logged in.
     * @param request Your controller's request object, we bind the login to this.
     */
    public void forceLogin(User user, HttpServletRequest request) {
        // Create a new Authentication with Username and Password (authorities here are
        // optional as the following function fetches these anyway)
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
                user.getPassword(), user.getAuthorities());
        // Authenticate the token properly with the CustomAuthenticationProvider
        Authentication authentication = authenticationManager.authenticate(token);
        // Check if the authentication is actually authenticated (in this example any
        // username/password is accepted so this should never be false)
        if (authentication.isAuthenticated()) {
            // Add the authentication to the current security context (Stateful)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Add the token to the request session (needed so the authentication can be
            // properly used)
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
        }
    }

    /**
     * Checks if user is 13 years or older in order to use the app
     * 
     * @param registerForm  The user form containing the age
     * @param bindingResult The object we'll attach errors to if it fails
     */
    private void checkAgeOnRegister(RegisterForm registerForm, BindingResult bindingResult) {
        Date dateOfBirth = registerForm.getDateOfBirth();
        LocalDate dateNow = LocalDate.now();
        LocalDate localDateOfBirth = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int age = Period.between(localDateOfBirth, dateNow).getYears();
        if (age < 13) {
            bindingResult.addError(
                    new FieldError("registerForm", "dateOfBirth", "You must be at least 13 years old to register"));
        }
    }

    /**
     * Checks if the email already exists
     * 
     * @param registerForm  The user form containing the email
     * @param bindingResult The object we'll attach errors to if it fails
     */
    private void checkEmailIsNotInUse(RegisterForm registerForm, BindingResult bindingResult) {
        String email = registerForm.getEmail();
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            bindingResult.addError(new FieldError("registerForm", "email", "Email is already in use"));
        }
    }

    /**
     * Checks if the passwords match and is secure enough (doesn't contain any other fields)
     *  The ACs state three vague security criteria (The PO didn't clarify beyond "Use common sense"):
     *   <ol>
     *          <li>At least 8 characters long (Jakarta annotations already handles this)</li>
     *          <li>Password doesn't "contain any other field", interpreted as your name can't be in the password.</li>
     *          <li>Password must "contain a variation of different character types", interpreted as at least one [lowercase, uppercase, digit, and symbol] each.</li>
     *   </ol>
     *
     * @param registerForm  The user form containing the password
     * @param bindingResult The object we'll attach errors to if it fails
     */
    private void checkPasswordsMatchAndIsSecure(RegisterForm registerForm, BindingResult bindingResult) {
        String password = registerForm.getPassword();
        String confirmPassword = registerForm.getConfirmPassword();
        // Check #1: Passwords match
        if (password.equals(confirmPassword)) {

            // Check #2: Password doesn't "contain any other field"
            String[] otherFields = new String[]{registerForm.getFirstName(), registerForm.getLastName(), registerForm.getEmail()};
            if(password.length() > 0) {
                for (String field : otherFields) {
                    if (field != "") {
                        if (password.toLowerCase().contains(field.toLowerCase())) {
                            bindingResult.addError(new FieldError("registerForm", "password", "Password can't contain values from other fields"));
                            break;
                        }
                    }
                }
            }
        }
        else {
            bindingResult.addError(new FieldError("registerForm", "password", "Passwords do not match"));

        }
    }



    /**
     * Gets form to be displayed
     * 
     * @param registerForm The registration form given to the user
     * @return thymeleaf register
     */
    @GetMapping("/register")
    public String getRegisterPage(
            RegisterForm registerForm, Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {
        logger.info("GET /register");
        URL url = new URL(httpServletRequest.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        String protocolAndAuthority = String.format("%s://%s", url.getProtocol(), url.getAuthority());
        model.addAttribute("httpServletRequest", httpServletRequest);
        model.addAttribute("countryCitySuburbNameRegex", countryCitySuburbNameRegex);
        model.addAttribute("addressRegex", addressRegex);
        model.addAttribute("postcodeRegex", postcodeRegex);
        model.addAttribute("path", path);
        return "register";
    }

    /**
     * Registers a user
     * @param registerForm The registration form given to the user
     * @param bindingResult Errors are stored here
     * @param request The controller's request object, we bind the login to this.
     * @return user page or register (if there are errors)
     */
    @PostMapping("/register")
    public String register(
            @Valid RegisterForm registerForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model) throws IOException {

        // Run the custom validation methods
        // TODO: Move validators that might be reused into their own class
        checkEmailIsNotInUse(registerForm, bindingResult);
        checkPasswordsMatchAndIsSecure(registerForm, bindingResult);
        model.addAttribute("httpServletRequest",request);

        if (bindingResult.hasErrors()) {
            URL url = new URL(request.getRequestURL().toString());
            String path = (url.getPath() + "/..");
            model.addAttribute("path", path);
            return "register";
        }

        User user = new User(registerForm.getFirstName(), registerForm.getLastName(), registerForm.getDateOfBirth(),
                registerForm.getEmail(), registerForm.getPassword(), new ArrayList<>(),
                new Location(registerForm.getAddressLine1(), registerForm.getAddressLine2(), registerForm.getSuburb(),
                        registerForm.getCity(), registerForm.getPostcode(), registerForm.getCountry()));

        user.grantAuthority("ROLE_USER");
        user = userService.updateOrAddUser(user);
        user.generateToken(userService,2);
        logger.info("The user token: " +user.getToken());

        // Auto-login when registering
        forceLogin(user, request);
        // This url will be added to the email
        String confirmationUrl = request.getRequestURL().toString().replace(request.getServletPath(), "")
                + "/confirm?token=" + user.getToken();

        System.out.println(confirmationUrl);
        logger.info(confirmationUrl);

        return "redirect:/user-info?name=" + user.getUserId();

    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        User user = userService.findByToken(token).get();
        if (user == null) {
            // Not sure if this will display the 404 page
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.confirmEmail();
        redirectAttributes.addFlashAttribute("message", "Your email has been confirmed successfully!");
        return "redirect:/login";
    }
}
