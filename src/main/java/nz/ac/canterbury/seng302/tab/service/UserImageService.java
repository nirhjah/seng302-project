package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserImageService extends FileDataSaver {

    /**
     * Writes files to the /{profile}/USER_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public UserImageService(@Value("${spring.profiles.active:unknown}") String profile) {
        super(getDeploymentType(profile));
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

    }
}
