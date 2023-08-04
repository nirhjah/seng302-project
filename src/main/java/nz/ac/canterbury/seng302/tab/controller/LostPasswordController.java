package nz.ac.canterbury.seng302.tab.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.LostPasswordForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
public class LostPasswordController {

    private final UserService userService;

    private static final String TEMPLATE_NAME = "lostPassword";

    public LostPasswordController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/lost-password")
    public String lostPasswordForm(Model model,HttpServletRequest request) {
        model.addAttribute("lostPasswordForm", new LostPasswordForm());
        model.addAttribute("httpServletRequest",request);
        return TEMPLATE_NAME;
    }

    /**
     *
     * @param email                 email for the reset password token link to be sent to
     * @param lostPasswordForm    forgot password form
     * @param bindingResult         errors attatched to this
     * @param model                 model to store model attributes
     * @param httpServletResponse   httpServerletResponse
     * @param request               request
     * @return forgot password page
     */
    @PostMapping("/lost-password")
    public String submitEmail(
            @RequestParam("email") String email,
            @Validated LostPasswordForm lostPasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request
    ) throws MessagingException {

        model.addAttribute("email", email);
        model.addAttribute("httpServletRequest",request);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            return TEMPLATE_NAME;
        }

        model.addAttribute("submitted_form_message", "If your email is registered with our system, you will receive a link to reset your password shortly.");

        Optional<User> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            userService.resetPasswordEmail(user.get(), request);
        }
        return TEMPLATE_NAME;
    }



}

