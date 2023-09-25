package nz.ac.canterbury.seng302.tab.controller;

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
     *
     * @param id
     * @return
     */
    @GetMapping("whiteboard-media/screenshot/{id}")
    public @ResponseBody ResponseEntity<byte[]> getPreview(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    // For video thumbnails:
    @GetMapping("whiteboard-media/thumbnail/{id}")
    public @ResponseBody ResponseEntity<byte[]> getThumbnail(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    /*
     We just yeet the bytes across, send all data at once, yolo
     */
    @GetMapping("whiteboard-media/video/{id}")
    public @ResponseBody ResponseEntity<byte[]> getRecording(@PathVariable long id) {
        logger.info("getRecording: {}", id);
        return whiteboardRecordingService.getRecording(id);
    }

    @PostMapping("whiteboard-media/save/video")
    public void setRecording(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamId") long teamId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        logger.info("POST setRecording: {}", teamId);
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                // TODO: Somehow save the thumbnail here.
                whiteboardRecordingService.createRecordingForTeam(file, team, isPublic);
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
    }


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
