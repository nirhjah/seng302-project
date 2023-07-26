package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProfilePictureController {
    private final UserImageService userImageService;
    private final TeamImageService teamImageService;
    private final ClubImageService clubImageService;

    @Autowired
    public ProfilePictureController(UserImageService userImageService, TeamImageService teamImageService, ClubImageService clubImageService) {
        this.userImageService = userImageService;
        this.teamImageService = teamImageService;
        this.clubImageService = clubImageService;
    }

    @GetMapping(
            value = "/user-profile-picture/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getUserProfilePicture(@PathVariable long id) {
        return userImageService.readFileOrDefault(id);
    }


    @GetMapping(
            value = "/team-profile-picture/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getTeamProfilePicture(@PathVariable long id) {
        return teamImageService.readFileOrDefault(id);
    }


    @GetMapping(
            value = "/club-logo/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getClubLogo(@PathVariable long id) {
        return clubImageService.readFileOrDefault(id);
    }
}

