package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    /**
     * Returns an SVG response, given imageData.
     * @param imageData The imagedata in bytes
     * @return a ResponseEntity that will be displayed on the webpage.
     */
    private ResponseEntity<byte[]> getSVGResponse(byte[] imageData) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/svg+xml");
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        return builder
                .headers(headers)
                .body(imageData);
    }


    /**
     * Gets a regular image response (jpg, png, or jpeg)
     * @param imageData The imagedata in bytes
     * @return a ResponseEntity that will be displayed on the webpage.
     */
    private ResponseEntity<byte[]> getImageResponse(byte[] imageData) {
        // It's a regular png/jpg
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        return builder
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    /**
     * Returns the profile picture of the user with id `id`.
     * @param id The user id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping(
            value = "/user-profile-picture/{id}"
    )
    @ResponseBody
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable long id) {
        /*
        TODO: We need to allow SVGs here!!!!
            Currently only pngs, jpgs, and jpegs are supported.
         */
        byte[] bytes = userImageService.readFileOrDefault(id);
        // We prolly want to store the image type inside of the entities themselves?
        // Maybe make a new column for it, mapping to enum.
        // It could also make more sense to store the file type inside of the FileDataSaver...?
        // Do some thinking about all this.
        boolean isSvg = false;

        if (isSvg) {
            return getSVGResponse(bytes);
        } else {
            return getImageResponse(bytes);
        }
    }

    /**
     * Returns the profile picture of the team with id `id`.
     * @param id The team id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping(
            value = "/team-profile-picture/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getTeamProfilePicture(@PathVariable long id) {
        return teamImageService.readFileOrDefault(id);
    }

    /**
     * NYI!!
     * Returns the profile picture of the team with id `id`.
     * @param id The team id in the database
     * @return A ResponseEntity containing the image data
     */
    @GetMapping(
            value = "/club-logo/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody ResponseEntity<byte[]> getClubLogo(@PathVariable long id) {
        // It's a regular png/jpg
        byte[] imageData = clubImageService.readFileOrDefault(id);

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        return builder
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);

    }
}

