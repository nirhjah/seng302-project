package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;

@Import({WhiteboardRecordingService.class})
@SpringBootTest
@WithMockUser
public class WhiteboardRecordingServiceTest {

    /*

    PLAN:
    What do we need to test???

    We need to make sure we test all abstract methods inside
    of abstract VideoService<>, or else they won't get coverage:

    We need to test that users can only access recordings
    if they are in a team that has the recording.

    Tests for abstract VideoService<>:
    - getVideoResponse
    - saveVideo

    Tests for WhiteboardRecoridngService:
    - canView
    - createRecordingForTeam
    - getRecording

     */

    @Autowired
    private WhiteboardRecordingService whiteboardRecordingService;

    @SpyBean
    private UserService userService;

    @Autowired
    private TeamService teamService;

    private User user;

    private Team team;

    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");

    private void setupUser() {
        try {
            user = User.defaultDummyUser();
        } catch (Exception ex1) {
            fail(ex1.getMessage());
        }
        String email = UUID.randomUUID() + "@gmail.com";
        user.setEmail(email);
        user = userService.updateOrAddUser(user);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @BeforeEach
    public void setUp(){
        assertEquals(FileDataSaver.DeploymentType.TEST, whiteboardRecordingService.getDeploymentType());
        setupUser();
        UserImageService.clearTestFolder();
        team = new Team("test", "Hockey", location);
        teamService.updateTeam(team);
    }

    @Test
    public void testGetRecording(){
        MultipartFile recording = new MockMultipartFile(
                "hello.jpg", new byte[] {1,2,3,4,5,6,7,8}
        );
    }

    @Test
    public void testCanView(){}

    @Test
    public void testCreateRecording(){}

    @Test
    public void testCreateRecordingForTeam(){}

    @Test
    public void testFindPublicPaginatedWhiteboardsBySports(){}

    @Test
    public void testGetAllPublicWhiteboardSports(){}

}
