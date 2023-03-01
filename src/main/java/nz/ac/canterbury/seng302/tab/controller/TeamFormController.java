package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * controller for the team form
 */
@Controller
public class TeamFormController {

    Logger logger = LoggerFactory.getLogger(TeamFormController.class);

    @Autowired
    private TeamService teamService;

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param displayTeamName previous name entered into form to be displayed
     * @param displayTeamSport previous favourite programming language entered into form to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/team_form")
    public String teamForm(@RequestParam(name="displayTeamName", required = false, defaultValue = "") String displayTeamName,
                       @RequestParam(name="displayTeamSport", required = false, defaultValue = "") String displayTeamSport,
                       @RequestParam(name="displayTeamLocation", required = false, defaultValue = "") String displayTeamLocation,
                       Model model) {
        logger.info("GET /team_form");
        model.addAttribute("displayTeamName", displayTeamName);
        model.addAttribute("teamSport", displayTeamSport);
        return "teamFormTemplate";
    }


    /**
     * Posts a form response with name and favourite language
     * @param name name if user
     * @param sport users team sport
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/team_form")
    public String submitTeamForm( @RequestParam(name="name") String name,
                              @RequestParam(name = "sport") String sport,
                              @RequestParam(name = "location") String location,
                              Model model) {
        logger.info("POST /team_form");
        Team team = new Team(name,location,sport);
        teamService.addTeam(team);
        model.addAttribute("profileFilePath",team.getPicturePath());
        model.addAttribute("displayTeamName", name);
        model.addAttribute("displayTeamLocation");
        model.addAttribute("displayTeamSport", sport);
        return "teamFormTemplate";

    }
}
