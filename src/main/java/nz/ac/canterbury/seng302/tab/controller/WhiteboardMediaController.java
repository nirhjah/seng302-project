package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * This controller handles saved media,
 * i.e. screenshots or recordings of the whiteboard.
 *  ----
 * Note that this controller shouldn't do any real html rendering,
 * rather, it should just deploy endpoints for other parts
 * of the codebase to access images/videos/thumbnails
 */
@Controller
public class WhiteboardMediaController {

    Logger logger = LoggerFactory.getLogger(WhiteboardMediaController.class);

    WhiteboardScreenshotService whiteboardScreenshotService;
    WhiteboardRecordingService whiteboardRecordingService;
    UserService userService;
    TeamService teamService;

    @Autowired
    public WhiteboardMediaController(WhiteboardScreenshotService whiteboardScreenshotService, WhiteboardRecordingService whiteboardRecordingService, UserService userService, TeamService teamService) {
        this.whiteboardScreenshotService = whiteboardScreenshotService;
        this.whiteboardRecordingService = whiteboardRecordingService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Gets screenshot by id
     * @param id screenshot id
     * @return screenshot
     */
    @GetMapping("whiteboard-media/screenshot/{id}")
    public @ResponseBody ResponseEntity<byte[]> getPreview(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    /**
     * Gets thumbnail for recording
     * @param id
     * @return
     */
    @GetMapping("whiteboard-media/thumbnail/{id}")
    public @ResponseBody ResponseEntity<byte[]> getThumbnail(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    /**
     * Gets recording by id
     * @param id recorded video id
     * @return recorded video
     */
    @GetMapping("whiteboard-media/video/{id}")
    public @ResponseBody ResponseEntity<byte[]> getRecording(@PathVariable long id) {
        logger.info("getRecording: {}", id);
        return whiteboardRecordingService.getRecording(id);
    }

    /**
     * Saves whiteboard recording to backend
     * @param file whiteboard recording file
     * @param teamId id of the team who is using the whiteboard
     * @param name name of the recording
     * @param isPublic set if video should be publicly or privately viewed
     * @param model model
     * @param httpServletRequest httpservletrequest
     * @return returns to team profile page upon saving video
     */
    @PostMapping("whiteboard-media/save/video")
    public String setRecording(
            @RequestParam("recording-input") MultipartFile file,
            @RequestParam("teamId") long teamId,
            @RequestParam("recording-name") String name,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic, Model model, HttpServletRequest httpServletRequest
    ) {
        model.addAttribute("httpServletRequest", httpServletRequest);
        logger.info("POST setRecording: {}", teamId);
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                // TODO: Somehow save the thumbnail here.
                whiteboardRecordingService.createRecordingForTeam(file, name, team, isPublic);
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
        return "redirect:/team-info?teamID=" + teamId;
    }


    /**
     * Saves whiteboard screenshot to backend
     * @param file whiteboard screenshot file
     * @param teamId id of the team who is using the whiteboard
     * @param isPublic set if video should be publicly or privately viewed
     */
    @PostMapping("whiteboard-media/save/screenshot")
    public void setScreenshot(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamId") long teamId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                whiteboardScreenshotService.createScreenshotForTeam(file, team, isPublic);
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
    }
}
