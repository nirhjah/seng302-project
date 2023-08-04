package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Configuration
@ComponentScan("nz.ac.canterbury.seng302.tab.service")
public class UserImageService extends ImageService<User> {

    private final UserService userService;

    private final byte[] defaultProfilePicture;

    /**
     * Writes files to the /{profile}/USER_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    @Autowired
    public UserImageService(@Value("${spring.profiles.active:unknown}") String profile, UserService userService) throws IOException {
        super(getDeploymentType(profile));
        this.userService = userService;

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

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.PNG_OR_JPEG;
    }

    /**
     * Updates a user's profile picture.
     * If the user doesn't exist, this method is a NOOP.
     *
     * @param userId The userId
     * @param file The file that represents the image
     */
    public void updateProfilePicture(long userId, MultipartFile file) {
        if (userService.findUserById(userId).isPresent()) {
            saveFile(userId, file);
        }
    }
}
