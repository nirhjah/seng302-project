package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@WithMockUser
public class TeamImageServiceTest {

    @Autowired
    private TeamImageService teamImageService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");

    User user = User.defaultDummyUser();

    /*
    This must be here, because defaultDummyUser throws IOException
     */
    public TeamImageServiceTest() throws IOException {
    }

    @BeforeEach
    void beforeEach() {
        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Test
    void ensureSuite() {
        // if this fails, the test suite won't run.
        assertTrue("no user", userService.getCurrentUser().isPresent());
    }

    @Test
    void testUpdatingPicture() throws IOException {
        Team team = new Team("test", "Hockey", location);
        team.setManager(user);
        teamRepository.save(team);

        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile("file.png",
                    file.getName(), "image/png", input.readAllBytes());
            teamImageService.updateProfilePicture(team.getTeamId(), multipartFile);
            assertArrayEquals(fileBytes, multipartFile.getBytes());
        }
    }
}
