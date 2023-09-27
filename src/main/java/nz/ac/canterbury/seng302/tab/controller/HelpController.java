package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {

    @GetMapping("/help")
    public String getHelpPage(HttpServletRequest request, Model model) {
        model.addAttribute("httpServletRequest", request);
        return "help";
    }
}

