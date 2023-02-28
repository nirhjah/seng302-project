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
        model.addAttribute("displayTeamLocation", displayTeamLocation);
        model.addAttribute("isSportValid", true);
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

//        String allUnicodeRegex = "\\p{L}\\s+";
        String allUnicodeRegex = "^[\\p{L}\\s]+$";
        String allUnicodeNotNumbersRegex = "[\\p{L}+^[0-9]]";
        // client side validation
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);

        // server side validation
        boolean nameValid = (name.matches(allUnicodeRegex));
        boolean sportValid = (sport.matches(allUnicodeRegex));
        boolean locationValid = (location.matches(allUnicodeRegex));
        if (!sportValid || !nameValid || !locationValid) {
            model.addAttribute("error", true);
            return "teamFormTemplate";
        }

        teamService.addTeam(new Team(name, location, sport));

        // handy for debugging
        model.addAttribute("displayTeamName", name);
        model.addAttribute("displayTeamLocation");
        model.addAttribute("displayTeamSport", sport);
        return "teamFormTemplate";

    }
}
