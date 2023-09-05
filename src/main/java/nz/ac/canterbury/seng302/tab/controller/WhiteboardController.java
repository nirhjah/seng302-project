package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WhiteboardController {

    @GetMapping("/whiteboard")
    public String getTemplate(Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest", request);
        return "whiteboardForm";
    }

}
