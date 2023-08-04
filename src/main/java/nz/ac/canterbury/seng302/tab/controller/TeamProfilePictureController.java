package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

@Controller
public class TeamProfilePictureController {
    private final TeamImageService teamImageService;

    @Autowired
    public TeamProfilePictureController(TeamImageService teamImageService) {
        this.teamImageService = teamImageService;
    }

    @GetMapping("/team-profile-picture/{id}")
    public @ResponseBody ResponseEntity<byte[]> getTeamProfilePicture(@PathVariable long id) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.
        return teamImageService.readFileOrDefault(id);
    }
}
