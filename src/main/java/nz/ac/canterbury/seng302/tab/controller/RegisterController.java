package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegisterController {
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (!password.equals(confirmPassword)) {
            bindingResult.addError(new FieldError("registerForm", "password", "Passwords do not match"));
        }

        // Check #2: Password doesn't "contain any other field"
        String[] otherFields = new String[]{registerForm.getFirstName(), registerForm.getLastName(), registerForm.getEmail()};
        if (!password.isEmpty()) {
            for (String field : otherFields) {
                if (!field.isEmpty() && password.toLowerCase().contains(field.toLowerCase())) {
                    bindingResult.addError(new FieldError("registerForm", "password", "Password can't contain values from other fields"));
                    break;
                }
            }
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
     * @throws ServletException This is thrown if login fails, which should never happen.
     */
    @PostMapping("/register")
    public String register(
            @Valid RegisterForm registerForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model, HttpSession session) throws IOException {

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

        String hashedPassword = passwordEncoder.encode(registerForm.getPassword());
        User user = new User(registerForm.getFirstName(), registerForm.getLastName(), registerForm.getDateOfBirth(),
                registerForm.getEmail(), hashedPassword, List.of(),
                new Location(registerForm.getAddressLine1(), registerForm.getAddressLine2(), registerForm.getSuburb(),
                        registerForm.getCity(), registerForm.getPostcode(), registerForm.getCountry()));

        user.grantAuthority("ROLE_USER");
        userService.updateOrAddUser(user);
        session.setAttribute("message", "An email has been sent to your email address. Please follow the instructions to validate your account before you can log in");
        return "redirect:/login";
    }
}
