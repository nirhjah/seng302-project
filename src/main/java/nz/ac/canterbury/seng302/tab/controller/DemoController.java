package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
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
     *
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");
        return "redirect:./demo";
    }

    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen with some links)
     *
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/demo")
    public String getTemplate(@RequestParam(name = "teamID", required = false) Long teamID, Model model) throws IOException {
        logger.info("GET /demo");
        if (teamService.getTeamList().size()<2) {
            teamService.addTeam(new Team("t", "t", "t"));
            teamService.addTeam(new Team("f", "f", "f"));
            for (int i = 0; i < 50; i++) {
                teamService.addTeam(new Team(String.valueOf(i), "f", "f"));
            }
        }
        if (teamID == null) {
            teamID = teamService.getTeamList().get(0).getTeamId();
        }
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("teamID", teamID);
        return "demoTemplate";
    }
}

