package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TeamImageService extends FileDataSaver {

    @Autowired
    private TeamService teamService;

    private final byte[] defaultProfilePicture;

    /**
     * Writes files to the /{profile}/TEAM_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public TeamImageService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile), FileDataSaver.DEFAULT_IMAGE_RESTRICTIONS);

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


    /**
     * Updates a team's profile picture.
     *
     * @param id The userId
     * @param file The file that represents the image
     */
    public void updateProfilePicture(long id, MultipartFile file) {
        // Optional<User> currentUser = getCurrentUser();
        // TODO: Check if the current user is a manager of team before changing.

        if (teamService.findTeamById(id).isPresent()) {
            saveFile(id, file);
        }
    }
}
