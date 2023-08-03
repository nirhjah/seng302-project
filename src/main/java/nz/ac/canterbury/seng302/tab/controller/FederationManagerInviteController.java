package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FederationManagerInviteController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    FederationService federationService;

    @Autowired
    UserService userService;

    String tok;

    @GetMapping("/federationManager")
    public String fedManagerInvitation(@RequestParam("token") String token, HttpServletRequest request, Model model) {
        model.addAttribute("httpServletRequest", request);
        tok = token;
        return "federationManagerInvite";
    }

    @PostMapping("/federationManager")
    public String federationManager(@RequestParam("token") String token, @RequestParam("decision") boolean decision, Model model) {
        if (decision) {
            User u = userService.getCurrentUser().get();
            //TODO Grant permission
            logger.info("FED MANAGER NOW");
        }
        return "redirect:user-info/self";
    }
}
