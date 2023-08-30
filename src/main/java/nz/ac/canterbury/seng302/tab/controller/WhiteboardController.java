package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WhiteboardController {

    @GetMapping("/whiteboard")
    public String getTemplate(Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest", request);
        return "whiteboardForm";
    }

}
