package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ClubImageService extends FileDataSaver {

    @Autowired
    private ClubService clubService;

    private final byte[] defaultClubLogo;

    /**
     * Writes files to the /{profile}/TEAM_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public ClubImageService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile));

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
     * @param bytes The bytes that represent the image
     */
    public void updateClubLogo(long id, byte[] bytes) {
        if (clubService.findClubById(id).isPresent()) {
            saveFile(id, bytes);
        }
    }
}
