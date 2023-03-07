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
 * Controller for profile form
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(ProfileFormController.class);

    @Autowired
    private TeamService teamService;
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param teamID team for which the details are to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */


    @GetMapping("/profileForm")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID) {
        logger.info("GET /profileForm");


        // Retrieve the selected team from the list of available teams using the ID
        // If the name is null or empty, return null
        List<Team> teamList = teamService.getTeamList();
        logger.info(teamList.get(0).toString());
        Team selectedTeam = null;
        if (teamID != null) {
            // Find the selected team by its id
            selectedTeam = teamList.stream()
                    .filter(team -> team.getTeamId().equals(teamID))
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("navTeams", teamList);
        model.addAttribute("teamID", teamID);
        model.addAttribute("displayName", selectedTeam.getName());
        model.addAttribute("displaySport", selectedTeam.getSport());
        model.addAttribute("displayLocation", selectedTeam.getLocation());

        return "profileForm";
    }
}



