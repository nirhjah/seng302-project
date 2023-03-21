package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ops.api.key}")
    private String apiKey;

    private String allUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";

    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     * @return thymeleaf createTeamForm
     */

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
            @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
            Model model) {
        model.addAttribute("apiKey", apiKey);
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
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);
        model.addAttribute("navTeams", teamService.getTeamList());
        return "createTeamForm";
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
    @PostMapping("/createTeam")
    public String submitTeamForm(
            @RequestParam(name = "teamID", defaultValue = "-1") long teamID,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "sport") String sport,
            @RequestParam (name = "address") String address,
            @RequestParam (name = "city") String city,
            @RequestParam (name ="country") String country,
            @RequestParam (name ="postcode") long postcode,
            @RequestParam(name ="suburb") String suburb,
            Model model) throws IOException {
        logger.info("POST /createTeam");

        // client side validation
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);

        // server side validation
        boolean nameValid = (name.matches(allUnicodeRegex));
        boolean sportValid = (sport.matches(allUnicodeRegex));
        if (!sportValid || !nameValid) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }
        Location locations = new Location (address, suburb, city, postcode, country);
        Team team;
        if ((team = teamService.getTeam(teamID)) != null) {
            team.setName(name);
            team.setSport(sport);
            teamService.updateTeam(team);
        } else {
            team = new Team(name, "REMOVE THIS", locations, sport);
            teamService.addTeam(team);
            teamID = team.getTeamId();
        }

        return String.format("redirect:./profile?teamID=%s", team.getTeamId());
    }
}
