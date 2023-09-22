package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
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
    UserService userService;
    TeamService teamService;

    @Autowired
    public WhiteboardMediaController(WhiteboardScreenshotService whiteboardScreenshotService, UserService userService, TeamService teamService) {
        this.whiteboardScreenshotService = whiteboardScreenshotService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("whiteboard-media/screenshot/{id}")
    public @ResponseBody ResponseEntity<byte[]> getPreview(@PathVariable(name="id") long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    // For video thumbnails:
    @GetMapping("whiteboard-media/thumbnail/{id}")
    public @ResponseBody ResponseEntity<byte[]> getThumbnail(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }

    /*
     Not sure if we should return a ResponseEntity here...
     we may need to look into data streaming, and stream the file across.
     Currently, the FDatasaver just loads the whole file into memory as bytes,
     and yeets it across. This won't work well for large recordings of 300mb or above,
     since itll put too much strain on our singular VM
     */
    @GetMapping("whiteboard-media/video/{id}")
    public @ResponseBody ResponseEntity<byte[]> getRecording(@PathVariable long id) {
        return whiteboardScreenshotService.getScreenshot(id);
    }


    @PostMapping("whiteboard-media/save/screenshot")
    public void setScreenshot(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamId") long teamId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic
    ) {
        Team team = teamService.getTeam(teamId);
        if (team != null) {
            whiteboardScreenshotService.createScreenshotForTeam(file, team, isPublic);
        } else {
            logger.warn("No team found with id: {}", teamId);
        }
    }
}
