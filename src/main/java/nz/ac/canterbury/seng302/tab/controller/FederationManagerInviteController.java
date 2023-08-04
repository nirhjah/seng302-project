package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
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

    FederationManagerInvite fedInvite;

    @GetMapping("/federationManager")
    public String fedManagerInvitation(@RequestParam("token") String token, HttpServletRequest request, Model model) {
        model.addAttribute("httpServletRequest", request);
        fedInvite = federationService.getByToken(token);
        return "federationManagerInvite";
    }

    @PostMapping("/federationManager")
    public String federationManager(@RequestParam(name = "decision") String decision) {
        boolean choice = Boolean.parseBoolean(decision);
        if (choice) {
            User u = userService.getCurrentUser().get();
            userService.updateOrAddUser(u);
            logger.info("FED MANAGER NOW");
        } else {
            logger.info("NOT FED MANAGER");
        }
        return "redirect:user-info/self";
    }
}
