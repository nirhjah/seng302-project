package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Comparator;
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
        model.addAttribute("httpServletRequest", request);

        Optional<List<LineUp>> teamLineUpsOpt = lineUpService.findLineUpsByTeam(teamID);
        List<LineUp> teamLineUps = teamLineUpsOpt.orElse(Collections.emptyList());

        Optional<Team> teamOpt = teamService.findTeamById(teamID);

        //Index of list of players equals the associated lineup
        List<List<User>> playersPerLineup = teamLineUps.stream().map(
                lineUp -> ViewActivityController(lineUp.getActivity().getId())
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

        model.addAttribute("playersPerLineup", playersPerLineup);

        return "whiteboardForm";
    }


    /**
     * returns the current players who are playing in the activity -- takes into account substitutions
     * @param actId the activity id
     * @return a list of the users that are currrently playing in the activity
     */
    private List<User> getAllPlayersCurrentlyPlaying(long actId) {
        List<User> playersPlaying = getAllPlayersPlaying(actId);
        Activity currActivity = activityService.findActivityById(actId);
        if (currActivity == null || currActivity.getTeam() == null) {
            return List.of();
        }
        List<Fact> allSubs = factService.getAllFactsOfGivenTypeForActivity(2, currActivity) // list of all made subs in the game
                .stream()   // We have to make a stream, because its actual type is UnmodifiableList, which you can't .sort()
                .sorted(Comparator.comparingInt(sub -> Integer.parseInt(sub.getTimeOfEvent())))  // all the subs sorted by time
                .toList();

        for (Fact fact : allSubs) {
            Substitution sub = (Substitution) fact;
            User playerOn = sub.getPlayerOn();
            User playerOff = sub.getPlayerOff();
            playersPlaying = playersPlaying.stream().map(player -> player.getUserId() == playerOff.getUserId() ? playerOn : player).toList();
        }

        return removeCoachesAndManager(currActivity.getTeam(), playersPlaying);
    }

    /**
     * returns a list of the users that are in the lineup
     * @param actId the activity id
     * @return a list of the users that are currently in the lineup for the activity, if there are no players the returns an empty list
     */
    private List<User> getAllPlayersPlaying(long actId) {
        Optional<List<LineUp>> optionalActivityLineups = lineUpService.findLineUpByActivity(actId);
        if (optionalActivityLineups.isEmpty()) {
            return List.of();
        }
        List<LineUp> activityLineups = optionalActivityLineups.get();

        if (activityLineups.isEmpty()) { // there is no lineup for some activities
            return List.of();
        }

        LineUp lineup = activityLineups.get(activityLineups.size() -1); // here we get the last one as that is the most recent one

        Optional<List<LineUpPosition>> optionaLineupPositions = lineUpPositionService.findLineUpPositionsByLineUp(lineup.getLineUpId());

        if (optionaLineupPositions.isEmpty()) {
            return List.of();
        }

        return optionaLineupPositions.get().stream().map(x -> x.getPlayer()).toList();
    }
}
