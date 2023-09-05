package nz.ac.canterbury.seng302.tab.service.image;

import nz.ac.canterbury.seng302.tab.entity.Screenshot;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ScreenshotService extends ImageService<Screenshot> {

    private static final String FOLDER_NAME = "SCREENSHOTS";

    @Autowired
    public ScreenshotService(@Value("${spring.profiles.active:unknown}") String profile) {
        super(getDeploymentType(profile));
        // Manually inject ScreenshotService into Screenshot entity:
        Screenshot.injectScreenshotService(this);
    }

    @Override
    public String getFolderName() {
        return FOLDER_NAME;
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.PNG_OR_JPEG;
    }

    public void deleteScreenshot(Screenshot screenshot) {
        deleteImage(screenshot);
    }
}
