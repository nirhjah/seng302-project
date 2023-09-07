package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring Boot Controller class for the whiteboard
 */
@Controller
public class WhiteboardController {

    private final Logger logger = LoggerFactory.getLogger(ViewUserController.class);

    @GetMapping("/whiteboard")
    public String getTemplate(Model model, HttpServletRequest request) {
        logger.info("GET /user-info");
        model.addAttribute("httpServletRequest", request);
        return "whiteboardForm";
    }

}
