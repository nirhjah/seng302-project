package nz.ac.canterbury.seng302.tab.service.image;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.repository.WhiteboardScreenshotRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
@Configuration
@ComponentScan("nz.ac.canterbury.seng302.tab.service")
public class WhiteboardScreenshotService extends ImageService<WhiteboardScreenshot> {

    private final Logger logger = LoggerFactory.getLogger(WhiteboardScreenshot.class);

    private final WhiteboardScreenshotRepository repository;
    private final TeamService teamService;
    private final UserService userService;

    /**
     * Writes files to the /{profile}/USER_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    @Autowired
    public WhiteboardScreenshotService(@Value("${spring.profiles.active:unknown}") String profile, WhiteboardScreenshotRepository repository, TeamService teamService, UserService userService) {
        super(getDeploymentType(profile));
        this.repository = repository;
        this.teamService = teamService;
        this.userService = userService;
    }

    /**
     * `prefix` is just the prefix before all files.
     * For example, these screenshots are saved as:
     * `WHITEBOARD_SCREENSHOTS/3489489389545`
     * `WHITEBOARD_SCREENSHOTS/5287274389549`
     * etc.
     */
    @Override
    public String getFolderName() {
        return "WHITEBOARD_SCREENSHOTS";
    }

    @Override
    public byte[] getDefaultBytes() {
        // TODO: Create a custom image here
        /*
        Actually, we need to do some digging down into FileDataSaver
        to see what actually happens when our entity isn't found.
         */
        return null;
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.PNG_OR_JPEG;
    }

    private boolean canView(WhiteboardScreenshot screenshot) {
        if (screenshot.isPublic()) {
            return true;
        }

        // Else, loop through all teams that this user is a part of.
        // If the screenshot belongs to any of the teams,
        // then return true, (else false.)
        Team team = screenshot.getTeam();
        User user = userService.getCurrentUser().orElseThrow();
        return (team.getTeamMembers().contains(user));
    }

    /**
     * Creates a new screenshot, and saves it.
     *
     * @param file The file that represents the screenshot
     * @return returns the saved screenshot with appropriate fields.
     */
    public WhiteboardScreenshot createScreenshot(MultipartFile file, boolean isPublic) {
        WhiteboardScreenshot screenshot = new WhiteboardScreenshot();
        saveImage(screenshot, file);
        screenshot.setPublic(isPublic);
        return repository.save(screenshot);
    }

    /**
     * Creates a screenshot, and immediately adds it to a team.
     * @param file The file with the image data
     * @param team The team in question
     * @param isPublic true if screenshot is public, false otherwise
     */
    public WhiteboardScreenshot createScreenshotForTeam(MultipartFile file, Team team, boolean isPublic) {
        WhiteboardScreenshot screenshot = createScreenshot(file, isPublic);
        team.addScreenshot(screenshot);
        team = teamService.updateTeam(team);
        screenshot.setTeam(team);
        return repository.save(screenshot);
    }


    public ResponseEntity<byte[]> getScreenshot(long id) {
        Optional<WhiteboardScreenshot> opt = repository.findById(id);
        if (opt.isPresent()) {
            WhiteboardScreenshot screenshot = opt.get();
            if (canView(screenshot)) {
                return getImageResponse(screenshot);
            }
        }
        // else, there's no content:
        return ResponseEntity.noContent().build();
    }
}
