package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the whiteboard
 */
@Controller
public class WhiteboardController {

    private FormationService formationService;
    private TeamService teamService;

    private LineUpService lineUpService;

    Team team;
    private final Logger logger = LoggerFactory.getLogger(WhiteboardController.class);


    public WhiteboardController(FormationService formationService, TeamService teamService, LineUpService lineUpService) {
        this.teamService = teamService;
        this.formationService = formationService;
        this.lineUpService = lineUpService;
    }



    /**
     * Gets the whiteboard page
     * @param teamID team ID used to get team lineup/formation/team members info
     * @param model  model the model to be filled
     * @param request the request
     * @return whiteboard page
     */
    @GetMapping("/whiteboard")
    public String getTemplate(@RequestParam(value = "teamID") Long teamID,
            Model model, HttpServletRequest request) {
        logger.info("GET /whiteboard");
        logger.info("team id" + teamID);
        model.addAttribute("httpServletRequest", request);

        Optional<List<LineUp>> teamLineUpsOpt = lineUpService.findLineUpsByTeam(teamID);
        List<LineUp> teamLineUps = teamLineUpsOpt.orElse(Collections.emptyList());

        if (teamService.findTeamById(teamID).isPresent()) {
            team = teamService.findTeamById(teamID).get();
        }
        else {
            return "homeForm";
        }

        model.addAttribute("teamFormations", formationService.getTeamsFormations(teamID));

        model.addAttribute("teamMembers", team.getNonManagersAndCoaches());

        model.addAttribute("teamLineUps", teamLineUps);

        return "whiteboardForm";
    }

}
