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

import java.io.IOException;

/**
 * Spring Boot Controller class for the Create Team Form
 */
@Controller
public class CreateTeamFormController {

    Logger logger = LoggerFactory.getLogger(CreateTeamFormController.class);

    @Autowired
    private TeamService teamService;

    /** A team name can be alphanumeric, dots and curly braces **/
    private final String teamNameUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";

    /** A sport can be letters, space, apostrophes or dashes **/
    private final String sportUnicodeRegex = "^[\\p{L}\\s\\'\\-]+$";

    /** A sport can be letters, space, apostrophes or dashes **/
    private final String locationUnicodeRegex = "^[\\p{L}\\s\\'\\-]+$";

    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     * @return thymeleaf createTeamForm
     */

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
            @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
            Model model) {
        logger.info("GET /createTeam");

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
        model.addAttribute("teamNameUnicodeRegex", teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", sportUnicodeRegex);
        model.addAttribute("locationUnicodeRegex", locationUnicodeRegex);

        model.addAttribute("navTeams", teamService.getTeamList());
        return "createTeamForm";
    }

    /**
     * Posts a form response with  team name, sport and location
     *
     * @param name  name if user
     * @param sport users team sport
     * @param model (map-like) representation of name, language and isJava boolean
     *              for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @param location the team's location
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/createTeam")
    public String submitTeamForm(
            @RequestParam(name = "teamID", defaultValue = "-1") long teamID,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "sport") String sport,
            @RequestParam(name = "location") String location,
            Model model) throws IOException {
        logger.info("POST /createTeam");

        // client side validation
        model.addAttribute("teamNameUnicodeRegex", teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", sportUnicodeRegex);
        model.addAttribute("locationUnicodeRegex", locationUnicodeRegex);

        // server side validation
        boolean nameValid = (name.matches(teamNameUnicodeRegex));
        boolean sportValid = (sport.matches(sportUnicodeRegex));
        boolean locationValid = (location.matches(locationUnicodeRegex));
        if (!sportValid || !nameValid || !locationValid) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }

        Team team = teamService.getTeam(teamID);
        if (team != null) { // Team hasn't been created yet
            team.setName(name);
            team.setSport(sport);
            team.setLocation(location);
            team = teamService.updateTeam(team);
        } else {
            team = new Team(name, location, sport);
            team = teamService.addTeam(team);
        }
        
        return String.format("redirect:./profile?teamID=%s", team.getTeamId());
    }
}
