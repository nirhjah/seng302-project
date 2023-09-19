package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({WhiteboardScreenshotService.class})
@SpringBootTest
public class WhiteBoardScreenshotServiceTest {

    @Autowired
    private WhiteboardScreenshotService whiteboardScreenshotService;

    @SpyBean
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private GenerateRandomUsers generateRandomUsers;


    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");

    private static final MultipartFile fakeScreenshot = new MockMultipartFile(
            "hello.jpg", new byte[] {1,2,3,4,5,6,7,8}
    );


    private Team team;
    private User user;

    private Set<WhiteboardScreenshot> publicScreenshots = new HashSet<>();
    private Set<WhiteboardScreenshot> privateScreenshots = new HashSet<>();

    private final int NUM_SCREENSHOTS = 5;

    void makeScreenshots() {
        for (int i=0; i<NUM_SCREENSHOTS; i++) {
            var priv = whiteboardScreenshotService.createScreenshotForTeam(fakeScreenshot, team, false);
            var pub = whiteboardScreenshotService.createScreenshotForTeam(fakeScreenshot, team, true);
            publicScreenshots.add(pub);
            privateScreenshots.add(priv);
        }
    }

    @BeforeEach
    void beforeEach() {
        // Check that we are on test.
        // If we aren't on test, we shouldn't run the test!!!
        // (This will mess up our filesystem on prod if it fails!!!)
        assertEquals(FileDataSaver.DeploymentType.TEST, whiteboardScreenshotService.getDeploymentType());

        privateScreenshots.clear();
        publicScreenshots.clear();

        // Clear files for test
        UserImageService.clearTestFolder();

        team = new Team("test", "Hockey", location);
        teamService.updateTeam(team);
    }

    private MockMultipartFile getMockedFile(byte[] data) {
        return new MockMultipartFile("/my_file.jpg", data);
    }

    @Test
    void testPrivateScreenshotsInvisible() {
        // Take a user, check that the file is saved.
        makeScreenshots();

        for (var priv: privateScreenshots) {
            var response = whiteboardScreenshotService.getScreenshot(priv.getId());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    void testPublicScreenshotsVisible() {
        // Take a user, check that the file is saved.
        makeScreenshots();

        for (var pub: publicScreenshots) {
            var response = whiteboardScreenshotService.getScreenshot(pub.getId());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @AfterAll
    static void afterAll() {
        // Clear files
        UserImageService.clearTestFolder();
    }
}
