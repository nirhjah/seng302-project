package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.nio.file.Files;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProfileFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @MockBean
    private UserService mockUserService;
    
    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeAll() throws IOException {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        teamRepository.save(team);
        ProfileFormController.teamId = team.getTeamId();

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782", "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Test
    public void testGettingTeamList() throws Exception {
        mockMvc.perform(get("/profile?teamID={id}", team.getTeamId())
                .requestAttr("teamID", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("profileForm"))
                .andExpect(MockMvcResultMatchers.model().attribute("teamID", team.getTeamId()))
                .andExpect(MockMvcResultMatchers.model().attribute("displayName", team.getName()))
                .andExpect(MockMvcResultMatchers.model().attribute("displaySport", team.getSport()))
                .andExpect(MockMvcResultMatchers.model().attribute("displayTeamPicture", team.getPictureString()));
    }

    @Test
    public void testUploadValidProfilePicture() throws Exception {
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input.readAllBytes());
            mockMvc.perform(multipart("/profile?teamID={id}", team.getTeamId()).file(multipartFile))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(String.format("./profile?teamID=%s", team.getTeamId())));
        }

    }

    @Test
    public void testUploadInvalidProfilePictureType() throws Exception {
        Resource resource = new ClassPathResource("/testingfiles/invalidFileType.txt");
        File file = resource.getFile();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                    input.readAllBytes());
            mockMvc.perform(multipart("/profile?teamID={id}", team.getTeamId()).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }
            assertNotEquals(team.getPictureString(), Base64.getEncoder().encodeToString(fileBytes));

    }

    @Test
    public void testUploadInvalidProfilePictureSize() throws Exception {
        Resource resource = new ClassPathResource("/testingfiles/maxFileSize.png");
        File file = resource.getFile();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png",
                    input.readAllBytes());
            mockMvc.perform(multipart("/profile?teamID={id}", team.getTeamId()).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }
        assertNotEquals(team.getPictureString(), Base64.getEncoder().encodeToString(fileBytes));
    }

}
