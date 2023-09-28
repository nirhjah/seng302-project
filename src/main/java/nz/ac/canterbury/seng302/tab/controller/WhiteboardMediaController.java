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
     * Gets screenshot by id
     * @param id screenshot id
     * @return screenshot
     */
    @GetMapping("whiteboard-media/screenshot/{id}")
    public @ResponseBody ResponseEntity<byte[]> getPreview(@PathVariable(name="id") long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    /**
     * Gets thumbnail for recording
     * @param id id of media to get thumbnail of
     * @return recording thumbnail
     */
    @GetMapping("whiteboard-media/thumbnail/{id}")
    public @ResponseBody ResponseEntity<byte[]> getThumbnail(@PathVariable long id) {
        logger.info("found endpoint");
        return whiteboardThumbnailService.getThumbnail(id);
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
     * @return returns to team profile page upon saving video
     */
    @PostMapping("whiteboard-media/save/video")
    public String setRecording(
            @RequestParam("recording-input") MultipartFile file,
            @RequestParam("teamIdForRecording") long teamId,
            @RequestParam("recording-name") String name,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        logger.info("/POST /whiteboard-media/save/screenshot");
        Team team = teamService.getTeam(teamId);
        User user = userService.getCurrentUser().orElseThrow();
        if (team != null) {
            if (team.isManagerOrCoach(user)) {
                WhiteBoardRecording whiteboard = whiteboardRecordingService.createRecordingForTeam(file, name, team, isPublic);
                try {
                    whiteboardThumbnailService.saveThumbnail(thumbnail, whiteboard);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            logger.warn("No team found with id: {}", teamId);
            return "redirect:/home";
        }
        return "redirect:/whiteboard?teamID=" + teamId;
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
