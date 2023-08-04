package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class TeamImageService extends ImageService<Team> {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    private final byte[] defaultProfilePicture;

    /**
     * Writes files to the /{profile}/TEAM_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public TeamImageService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile));

        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        defaultProfilePicture = is.readAllBytes();
    }

    /**
     * `prefix` is just the prefix before all files.
     * For example, teams prof picture saving should have a prefix like "team_imgs"
     * And all the files are saved similar to so:
     * `team_imgs_3489489389545`
     * `team_imgs_5287274389549`
     * etc.
     */
    @Override
    public String getFolderName() {
        return "TEAM_PROFILE_PICTURES";
    }

    @Override
    public byte[] getDefaultBytes() {
        return defaultProfilePicture;
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.PNG_OR_JPEG;
    }

    /**
     * Updates a team's profile picture.
     * If the current user isn't a manager or coach of the team, this operation is denied.
     *
     * @param id The userId
     * @param file The file that represents the image
     */
    public void updateProfilePicture(long id, MultipartFile file) {
        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            // No current user.
            return;
        }

        User user = optUser.get();
        Optional<Team> optTeam = teamService.findTeamById(id);
        if (optTeam.isEmpty()) {
            // no team
            return;
        }

        Team team = optTeam.get();
        if (team.isManager(user) || team.isCoach(user)) {
            // Now, only save the file if the user is a manager or coach.
            // else, fail
            saveFile(id, file);
        }
    }

    public ResponseEntity<byte[]> getImageResponse(long id) {
        Optional<Team> optTeam = teamService.findTeamById(id);
        if (optTeam.isPresent()) {
            return getImageResponse(optTeam.get());
        }
        return ResponseEntity.noContent().build();
    }
}
