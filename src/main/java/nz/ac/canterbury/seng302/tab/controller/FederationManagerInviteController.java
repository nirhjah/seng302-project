package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FederationManagerInviteController {

    @GetMapping("/federationManager")
    public String fedManagerInvitation(HttpServletRequest request, Model model) {
        model.addAttribute("httpServletRequest", request);
        return "federationManagerInvite";
    }
}
