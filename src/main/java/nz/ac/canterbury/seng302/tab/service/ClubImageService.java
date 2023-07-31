package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.controller.EditUserFormController;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ClubImageService extends FileDataSaver {

    private final Logger logger = LoggerFactory.getLogger(ClubImageService.class);

    @Autowired
    private ClubService clubService;

    private final byte[] defaultClubLogo;

    /**
     * Writes files to the /{profile}/TEAM_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public ClubImageService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile), FileDataSaver.DEFAULT_IMAGE_RESTRICTIONS);

        // TODO: We probably need to have a different default club logo here.
        //  maybe a shield or banner or something?
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        defaultClubLogo = is.readAllBytes();
    }

    @Override
    public String getFolderName() {
        return "CLUB_LOGOS";
    }

    @Override
    public byte[] getDefaultBytes() {
        return defaultClubLogo;
    }


    /**
     * Updates a club logo
     *
     * @param id The userId
     * @param file The file that represents the image
     */
    public void updateClubLogo(long id, MultipartFile file) {
        if (clubService.findClubById(id).isPresent()) {
            boolean ok = saveFile(id, file);
            if (!ok) {
                logger.error("Couldn't save file: " + id);
            }
        }
    }
}
