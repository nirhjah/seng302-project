package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TeamImageService extends FileDataSaver {

    private final byte[] defaultProfilePicture;

    /**
     * Writes files to the /{profile}/USER_PROFILE_PICTURES/ folder
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
        return "USER_PROFILE_PICTURES";
    }

    @Override
    public byte[] getDefaultBytes() {
        return defaultProfilePicture;
    }


    /**
     * Updates a team's profile picture.
     *
     * @param id The userId
     * @param bytes The bytes that represent the image
     */
    public void updateProfilePicture(long id, byte[] bytes) {
        saveFile(id, bytes);
    }
}
