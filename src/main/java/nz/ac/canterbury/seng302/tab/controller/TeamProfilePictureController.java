package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TeamProfilePictureController {
    private final TeamImageService teamImageService;

    @Autowired
    public TeamProfilePictureController(TeamImageService teamImageService) {
        this.teamImageService = teamImageService;
    }

    @GetMapping("/team-profile-picture/{id}")
    public @ResponseBody String getTeamProfilePicture(@PathVariable long id) {
        return teamImageService.readFileOrDefaultB64(id);
    }
}
