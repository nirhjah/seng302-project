package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.response.LineUpInfo;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Spring Boot Controller class for the whiteboard
 */
@Controller
public class WhiteboardController {

    private final FormationService formationService;
    private final TeamService teamService;

    private final LineUpService lineUpService;

    private final ActivityService activityService;

    private final LineUpPositionService lineUpPositionService;

    Team team;
    private final Logger logger = LoggerFactory.getLogger(WhiteboardController.class);


    public WhiteboardController(FormationService formationService, TeamService teamService, LineUpService lineUpService, ActivityService activityService, LineUpPositionService lineUpPositionService) {
        this.teamService = teamService;
        this.formationService = formationService;
        this.lineUpService = lineUpService;
        this.activityService = activityService;
        this.lineUpPositionService = lineUpPositionService;
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
        model.addAttribute("httpServletRequest", request);

        Optional<List<LineUp>> teamLineUpsOpt = lineUpService.findLineUpsByTeam(teamID);
        List<LineUp> teamLineUps = teamLineUpsOpt.orElse(Collections.emptyList());

        Optional<Team> teamOpt = teamService.findTeamById(teamID);

        //Index of list of players equals the associated lineup
        List<List<User>> playersPerLineup = teamLineUps.stream().map(
                lineUp -> {
                    long id = lineUp.getActivity().getId();
                    return activityService.getAllPlayersPlaying(id);
                }
        ).toList();


        if (teamOpt.isPresent()) {
            team = teamOpt.get();
        }
        else {
            return "homeForm";
        }

        model.addAttribute("teamFormations", formationService.getTeamsFormations(teamID));

        model.addAttribute("teamMembers", team.getNonManagersAndCoaches());

        model.addAttribute("teamLineUps", teamLineUps);

        List<List<LineUpPosition>> positionsList = new ArrayList<>();
        model.addAttribute("teamLineupsPositions", positionsList);

        model.addAttribute("playersPerLineup", playersPerLineup);

        Map<Long, LineUpInfo> map = new HashMap<>();

        for (LineUp lineup : teamLineUps) {
            var opt = lineUpPositionService.findLineUpPositionsByLineUp(lineup.getLineUpId());
            if (opt.isPresent()) {
                var linfo = new LineUpInfo(lineup, opt.get());
                map.put(lineup.getLineUpId(), linfo);
            }
        }

        model.addAttribute("lineUpToLineUpPositions", map);

        return "whiteboardForm";
    }

    private LineUpInfo getLineUpInfo(LineUp lineup) {
        Optional<List<LineUpPosition>> lineUpPositions = lineUpPositionService.findLineUpPositionsByLineUp(lineup.getLineUpId());
        if (lineUpPositions.isPresent()) {
            new LineUpInfo(lineup, lineUpPositions.get());
        }
        return null;
    }
}
