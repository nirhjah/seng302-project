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

    private String allUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";

    /**
     * Gets form to be displayed, includes the ability to display results of
     * previous form when linked to from POST form
     *
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/team_form")
    public String teamForm(@RequestParam(name = "edit", defaultValue = "-1") long teamID,
            Model model) {
        logger.info("GET /team_form");

        Team team;
        if (teamID != -1 && (team = teamService.getTeam(teamID)) != null) {
            model.addAttribute("name", team.getName());
            model.addAttribute("sport", team.getSport());
            model.addAttribute("location", team.getLocation());
            model.addAttribute("teamID", team.getTeamId());
        } else {
            model.addAttribute("error", "Invalid team ID, creating a new team instead.");
        }

        // client side validation
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);

        return "teamFormTemplate";
    }

    /**
     * Posts a form response with name and favourite language
     *
     * @param name  name if user
     * @param sport users team sport
     * @param model (map-like) representation of name, language and isJava boolean
     *              for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/team_form")
    public String submitTeamForm(@RequestParam(name = "name") String name,
            @RequestParam(name = "sport") String sport,
            @RequestParam(name = "location") String location,
            Model model) {
        logger.info("POST /team_form");

        // client side validation
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);

        // server side validation
        boolean nameValid = (name.matches(allUnicodeRegex));
        boolean sportValid = (sport.matches(allUnicodeRegex));
        boolean locationValid = (location.matches(allUnicodeRegex));
        if (!sportValid || !nameValid || !locationValid) {
            model.addAttribute("error", "An error occurred");
            return "teamFormTemplate";
        }

        Team newTeam = new Team(name, location, sport);
        teamService.addTeam(newTeam);
        return String.format("redirect:./profileForm?teamID=%s", newTeam.getTeamId());

    }
}
