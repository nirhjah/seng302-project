package nz.ac.canterbury.seng302.tab.service.image;

import jakarta.transaction.Transactional;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


@Service
@Configuration
@ComponentScan("nz.ac.canterbury.seng302.tab.service")
public class WhiteboardScreenshotService extends ImageService<WhiteboardScreenshot> {

    private final WhiteboardScreenshotRepository repository;
    private final TeamService teamService;
    private final UserService userService;

    private final byte[] defaultBytes;

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

        try {
            Resource resource = new ClassPathResource("/static/image/pitches/soccer.svg");
            InputStream is = resource.getInputStream();
            defaultBytes = is.readAllBytes();
        } catch (IOException iox) {
            throw new RuntimeException("Couldn't initialize WhiteBoardScreenshotService");
        }
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
        /*
        We should do some digging into FileDataSaver
        to see what actually happens when our entity isn't found.
         */
        return defaultBytes;
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.SVG;
    }

    public boolean canView(WhiteboardScreenshot screenshot) {
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
    public WhiteboardScreenshot createScreenshot(MultipartFile file, String tag, boolean isPublic) {
        WhiteboardScreenshot screenshot = new WhiteboardScreenshot();
        screenshot.setPublic(isPublic);
        screenshot.setTag(tag);
        // We must save here first, so that an id is allocated to the entity
        screenshot = repository.save(screenshot);
        saveImage(screenshot, file);
        // We must save here AGAIN, because our `saveImage` method mutates the entity.
        screenshot = repository.save(screenshot);
        return screenshot;
    }

    /**
     * Creates a screenshot, and immediately adds it to a team.
     * @param file The file with the image data
     * @param team The team in question
     * @param isPublic true if screenshot is public, false otherwise
     */
    public WhiteboardScreenshot createScreenshotForTeam(MultipartFile file, String tag, Team team, boolean isPublic) {
        WhiteboardScreenshot screenshot = createScreenshot(file, tag, isPublic);
        screenshot.setTeam(team);
        screenshot = repository.save(screenshot);
        team.addScreenshot(screenshot);
        teamService.updateTeam(team);
        return screenshot;
    }


    /*
    We need @Transactional to get around LazyInitializationException.
     https://stackoverflow.com/questions/21574236/how-to-fix-org-hibernate-lazyinitializationexception-could-not-initialize-prox
     For future, we need to be wary about where we are accessing the screenshots as lazy.

    Also, this method needs to be public, or else @Transactional wont work.

    Not only that, but @Transactional ONLY WORKS when it is called from outside the bean.
    Further reading here:
    https://codete.com/blog/5-common-spring-transactional-pitfalls
     */
    @Transactional
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
