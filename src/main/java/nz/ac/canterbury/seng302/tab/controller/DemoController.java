package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class DemoController {
    Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private TeamService teamService;
    /**
     * Redirects GET default url '/' to '/demo'
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");
        return "redirect:./demo";
    }

    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen with some links)
     * @param name url query parameter of user's name
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/demo")
    public String getTemplate(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        logger.info("GET /demo");
        model.addAttribute("name", name);
        model.addAttribute("displayTeams", teamService.getTeamList());
        return "demoTemplate";
    }

}
