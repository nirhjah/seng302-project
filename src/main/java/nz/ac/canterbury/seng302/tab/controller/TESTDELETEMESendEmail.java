package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class TESTDELETEMESendEmail {
    
    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @GetMapping("/TEST-send-email")
    public String sendTestEmail(
        @RequestParam("email") String email,
        HttpServletRequest request
    ) {
        User user = userService.getCurrentUser().orElseThrow();
        user.setEmail(email);
        user.setToken("IThinkYouAreCool");
        emailService.resetPasswordEmail(user, request);
        return "redirect:home";
    }
}
