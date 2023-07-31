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

/**
 * ProfilePictureController.
 * -
 * This controller takes URLs as input, and spits out images.
 * (Usually, the URL contains the JPA id of whatever entity owns the profile picture.)
 * -
 * For example:  user-profiles-pictures/120934 will return the profile picture
 * of user `120934`, as a jpg.
 * If the entity doesn't exist, then a default image is returned. (For example, default user pfp.)
 * Try it if you are curious!
 * -
 * Note that we are using jpg, but actually, other image types would work too.
 */
@Controller
public class ProfilePictureController {

    /**
     * All of these services inherit from the abstract FileDataSaver class.
     * Most of the logic is in there.
     */
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

