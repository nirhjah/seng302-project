package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
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
//
//    @Value("${ops.api.key}")
//    private String apiKey;

    private String allUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";
    private String addressRegex= "^\\d+\\s[A-z]+\\s[A-z]+";
    private String countryNameRegex= "^[A-Z][a-z]+( [A-Z][a-z]+)*$";
    private String cityNameRegex= "^([a-zA-Z\u0080-\u024F]+(?:. |-| |'))*[a-zA-Z\u0080-\u024F]*$";
    private String postcodeRegex= "(?i)^[a-z0-9][a-z0-9\\- ]{0,10}[a-z0-9]$";

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
        model.addAttribute("addressRegex", addressRegex);
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);
        model.addAttribute("countryNameRegex",countryNameRegex);
        model.addAttribute("cityNameRegex",cityNameRegex);
        model.addAttribute("postcodeRegex",postcodeRegex);
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
            @RequestParam (name = "addressLine1") String addressLine1,
            @RequestParam (name = "addressLine2") String addressLine2,
            @RequestParam (name = "city") String city,
            @RequestParam (name ="country") String country,
            @RequestParam (name ="postcode") String postcode,
            @RequestParam(name ="suburb") String suburb,
            Model model) throws IOException {
        logger.info("POST /createTeam");

        // client side validation
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);
        model.addAttribute("addressRegex",addressRegex);
        model.addAttribute("countryNameRegex",countryNameRegex);
        model.addAttribute("cityNameRegex",cityNameRegex);
        model.addAttribute("postcodeRegex",postcodeRegex);

        // server side validation
        boolean nameValid = (name.matches(allUnicodeRegex));
        boolean sportValid = (sport.matches(allUnicodeRegex));
        boolean address1Valid= (addressLine1.matches(addressRegex));
        boolean address2Valid= (addressLine2.matches(addressRegex)||addressLine2.matches(cityNameRegex));
        boolean countryValid = (country.matches(countryNameRegex));
        boolean cityValid = (city.matches(cityNameRegex));
        boolean postcodeValid = (postcode.matches(postcodeRegex));
        boolean suburbValid = (suburb.matches(cityNameRegex));
        if (!sportValid || !nameValid||!address1Valid||!countryValid||!cityValid||!postcodeValid||!suburbValid||!address2Valid) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }
        Location location = new Location (addressLine1, addressLine2, suburb, city, postcode, country);
        Team team;
        if ((team = teamService.getTeam(teamID)) != null) {
            team.setName(name);
            team.setSport(sport);
            team.setLocation(location);
            teamService.updateTeam(team);
        } else {
            team = new Team(name, sport, location);
            teamService.addTeam(team);
            teamID = team.getTeamId();
        }

        return String.format("redirect:./profile?teamID=%s", team.getTeamId());
    }
}
