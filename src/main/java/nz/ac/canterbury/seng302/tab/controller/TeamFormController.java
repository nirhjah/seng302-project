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
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
            @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
            Model model) {
        logger.info("GET /team_form");

        Team team;
        if (teamID != null) {
            if ((team = teamService.getTeam(teamID)) != null) {
                model.addAttribute("name", team.getName());
                model.addAttribute("sport", team.getSport());
                model.addAttribute("location", team.getLocation());
                model.addAttribute("teamID", team.getTeamId());
            } else {
                model.addAttribute("invalid_team", "Invalid team ID, creating a new team instead.");
            }
        }

        if (invalidInput) {
            model.addAttribute("invalid_input", "Invalid input.");
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
    public String submitTeamForm(
            @RequestParam(name = "teamID", defaultValue = "-1") long teamID,
            @RequestParam(name = "name") String name,
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
            return "redirect:./team_form?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }

        Team team;
        if ((team = teamService.getTeam(teamID)) != null) {
            team.setName(name);
            team.setSport(sport);
            team.setLocation(location);
            teamService.updateTeam(team);
        } else {
            team = new Team(name, location, sport);
            teamService.addTeam(team);
            teamID = team.getTeamId();
        }

        return String.format("redirect:./profileForm?teamID=%s", team.getTeamId());

    }
}
