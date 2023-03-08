package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class HomeFormController {
    Logger logger = LoggerFactory.getLogger(HomeFormController.class);

    @Autowired
    private TeamService teamService;

    /**
     * Redirects GET default url '/' to '/home'
     *
     * @return redirect to /home
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /homeForm");
        return "redirect:./home";
    }

    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen with some links)
     *
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/home")
    public String getTemplate(Model model) throws IOException {
        logger.info("GET /homeForm");
        if (teamService.getTeamList().size()<2) {
            teamService.addTeam(new Team("t", "t", "t"));
            teamService.addTeam(new Team("f", "f", "f"));
            for (int i = 0; i < 50; i++) {
                teamService.addTeam(new Team(String.valueOf(i), "f", "f"));
            }
        }
        model.addAttribute("navTeams", teamService.getTeamList());
        return "homeForm";
    }
}

