package nz.ac.canterbury.seng302.tab.service.video;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class WhiteboardRecordingService extends FileDataSaver {

    private static final String FOLDER_NAME = "WHITEBOARD_MEDIA_RECORDINGS";


    /**
     * Writes files to the /{profile}/WHITEBOARD_MEDIA_RECORDINGS/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public WhiteboardRecordingService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile));

        Resource resource = new ClassPathResource("/static/image/icons/club-logo.svg");
        InputStream is = resource.getInputStream();
        defaultClubLogo = is.readAllBytes();
    }

    @Override
    public String getFolderName() {
        return FOLDER_NAME;
    }
}
