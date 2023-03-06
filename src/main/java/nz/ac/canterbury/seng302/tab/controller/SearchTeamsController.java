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

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.tab.repository.TeamRepository;

/**
 * Controller for profile form
 */
@Controller
public class SearchTeamsController {

    Logger logger = LoggerFactory.getLogger(nz.ac.canterbury.seng302.tab.controller.SearchTeamsController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * Gets form to be displayed
     *
     * @param teamName name of the team that has been searched for by the database
     * @param model    (map-like) representation of teamID and teamFilter
     * @return thymeleaf searchTeamsForm
     */
    @GetMapping("/searchTeams")
    public String searchTeams(@RequestParam(value = "teamName", required = false) String teamName,
                              Model model) {
        model.addAttribute("notSearch", false);
        if (teamName != null) {
            if (teamName.length() < 3) {
                model.addAttribute("error", "team name must be at least 3 characters long");
                model.addAttribute("teams", new ArrayList<Team>());
            } else {
                List<Team> teams = teamRepository.findTeamByName(teamName);
                model.addAttribute("teams", teams);
            }
        } else {
            model.addAttribute("teams", new ArrayList<Team>());
            model.addAttribute("notSearch", true);
        }
        return "searchTeamsForm";
    }
}
