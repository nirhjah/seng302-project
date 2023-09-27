package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardThumbnailService;
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

    WhiteboardThumbnailService whiteboardThumbnailService;
    UserService userService;
    TeamService teamService;

    @Autowired
    public WhiteboardMediaController(WhiteboardScreenshotService whiteboardScreenshotService, WhiteboardRecordingService whiteboardRecordingService, UserService userService, TeamService teamService, WhiteboardThumbnailService whiteboardThumbnailService) {
        this.whiteboardScreenshotService = whiteboardScreenshotService;
        this.whiteboardRecordingService = whiteboardRecordingService;
        this.whiteboardThumbnailService = whiteboardThumbnailService;
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
        return whiteboardThumbnailService.getThumbnail(id);
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
            @RequestParam("recording") MultipartFile recording,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("teamId") long teamId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        logger.info("POST setRecording: {}", teamId);
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                WhiteBoardRecording whiteboard = whiteboardRecordingService.createRecordingForTeam(recording, team, isPublic);
                try {
                    whiteboardThumbnailService.saveThumbnail(thumbnail, whiteboard);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
    }


    /**
     * persists the whiteboard screenshot to the backend
     * @param file the file to be saved
     * @param teamId the team that the screenshot belongs to
     * @param name the 'tag' of the whiteboard
     * @param isPublic if the whiteboard is public or private
     * @return redirect to the whiteboard page
    */
    @PostMapping("whiteboard-media/save/screenshot")
    public String setScreenshot(
            @RequestParam("screenshot-input") MultipartFile file,
            @RequestParam("teamId") long teamId,
            @RequestParam("screenshot-name") String name,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        logger.info("/POST /whiteboard-media/save/screenshot");
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                whiteboardScreenshotService.createScreenshotForTeam(file, name, team, isPublic);
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
        // maybe redirect to view the whiteboard
        return "redirect:/whiteboard?teamID=" + team.getId();
    }
}
