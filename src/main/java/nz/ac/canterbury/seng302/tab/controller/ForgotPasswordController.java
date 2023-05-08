package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.ForgotPasswordForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Forgot Password Class.
 */
@Controller
public class ForgotPasswordController {

    Optional<User> user;

    User currentUser;

    @Autowired
    UserService userService;
    Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model,HttpServletRequest request) {
        model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        model.addAttribute("httpServletRequest",request);
        return "forgotPassword";
    }

    /**
     *
     * @param email                 email for the reset password token link to be sent to
     * @param forgotPasswordForm    forgot password form
     * @param bindingResult         errors attatched to this
     * @param model                 model to store model attributes
     * @param httpServletResponse   httpServerletResponse
     * @param request               request
     * @return
     */
    @PostMapping("/forgot-password")
    public String submitEmail(
            @RequestParam("email") String email,
            @Validated ForgotPasswordForm forgotPasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request
    ) {

        model.addAttribute("email", email);
        model.addAttribute("httpServletRequest",request);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return "forgotPassword";
        }

        model.addAttribute("submitted_form_message", "If your email is registered with our system, you will receive a link to reset your password shortly.");

        user = userService.findUserByEmail(email);


        if (user.isPresent()) {
            currentUser = user.get();

            currentUser.generateToken(userService, 1);

            String tokenVerificationLink = request.getRequestURL().toString().replace(request.getServletPath(), "") + "/reset-password/" + currentUser.getToken();

            logger.info("Link to reset password: " + tokenVerificationLink);

            userService.updateOrAddUser(currentUser);

        }

        return "forgotPassword";
    }



}

