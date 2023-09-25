package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.response.LineUpInfo;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        // Throw a 404 if the specified team doesn't exist
        Team team = teamService.findTeamById(teamID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Team does not exist"));

        //Index of list of players equals the associated lineup
        List<List<User>> playersPerLineup = teamLineUps.stream().map(
                lineUp -> {
                    long id = lineUp.getActivity().getId();
                    return activityService.getAllPlayersPlaying(id);
                }
        ).toList();

        model.addAttribute("teamFormations", formationService.getTeamsFormations(teamID));

        model.addAttribute("teamMembers", team.getNonManagersAndCoaches());

        model.addAttribute("teamLineUps", teamLineUps);

        List<List<LineUpPosition>> positionsList = new ArrayList<>();
        model.addAttribute("teamLineupsPositions", positionsList);

        model.addAttribute("playersPerLineup", playersPerLineup);

        return "whiteboardForm";
    }

    @GetMapping(path = "/whiteboard/get-lineup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineUpInfo> getLineUpJSON(@RequestParam("lineUpId") long lineUpId) {
        Optional<LineUp> optLineUp = lineUpService.findLineUpById(lineUpId);
        if (optLineUp.isPresent()) {
            LineUp lineUp = optLineUp.get();
            Optional<List<LineUpPosition>> lineUpPositions = lineUpPositionService.findLineUpPositionsByLineUp(lineUpId);
            if (lineUpPositions.isPresent()) {
                LineUpInfo lineUpInfo = new LineUpInfo(lineUp, lineUpPositions.get());
                List<Long> subs = activityService.getAllPlayerSubstitutes(lineUp.getActivity().getId())
                        .stream()
                        .map(user -> user.getId())
                        .toList();
                lineUpInfo.setSubs(subs);
                return ResponseEntity.ok().body(lineUpInfo);
            }
        }
        return ResponseEntity.notFound().build();
    }


    // testing controller
    @PostMapping("/whiteboard/save-recording")
    public ResponseEntity<String> saveWhiteboardRecording(
            @RequestParam("recording-input") MultipartFile whiteboardRecording,
            @RequestParam("recording-name") String name) throws IOException {

        logger.info("POST /whiteboard/save-recording");
        return ResponseEntity.ok(
                String.format("""
            Name='%s'<br>
            whiteboardRecording's size=%d<br>
            <video width='320' height='240' controls>
            <source src="data:video/mp4;base64,%s" type="video/mp4">
            </video>
            """,
                        name,
                        whiteboardRecording.getSize(),
                        Base64.getEncoder().encodeToString(whiteboardRecording.getBytes())
                )
        );
    }

    // TESTING CONTROLLER, PLEASE DELETE
    @PostMapping("/whiteboard/upload-screenshot")
    public ResponseEntity<String> uploadWhiteboardScreenshot(
            @RequestParam("screenshot-input") MultipartFile whiteboardScreenshot,
            @RequestParam("screenshot-name") String name) throws IOException {

        logger.info("POST /whiteboard/upload-screenshot");
        return ResponseEntity.ok(
            String.format("""
            Name='%s'<br>
            whiteboardScreenshot's size=%d<br>
            <img src="data:image/png;base64,%s" />
            """,
            name,
            whiteboardScreenshot.getSize(),
            Base64.getEncoder().encodeToString(whiteboardScreenshot.getBytes())
            )
        );
    }


}
