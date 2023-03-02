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
public class ViewTeamsFormController {

    Logger logger = LoggerFactory.getLogger(ViewTeamsFormController.class);

    @Autowired
    private TeamService teamService;
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param teamID team for which the details are to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */


    @GetMapping("/view_teams_form")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID) {
        logger.info("GET /view_teams_form");

        List<Team> teamList = teamService.getTeamList();

        model.addAttribute("displayTeams", teamList);
        model.addAttribute("teamID", teamID);
        return "viewTeamsForm";
    }
}