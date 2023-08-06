package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


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

    /**
     * Returns the profile picture of the user with id `id`.
     * @param id The user id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping("/user-profile-picture/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable long id) {
        return userImageService.getImageResponse(id);
    }

    /**
     * Returns the profile picture of the team with id `id`.
     * @param id The team id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping("/team-profile-picture/{id}")
    public @ResponseBody ResponseEntity<byte[]> getTeamProfilePicture(@PathVariable long id) {
        return teamImageService.getImageResponse(id);
    }

    /**
     * Returns the profile picture of the club with id `id`.
     * @param id The team id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping("/club-logo/{id}")
    public @ResponseBody ResponseEntity<byte[]> getClubLogo(@PathVariable long id) {
        return clubImageService.getImageResponse(id);
    }
}

