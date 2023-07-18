package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class ProfileFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private TeamService mockTeamService;

    private User user;
    private Team team;

    private static final Long TEAM_ID = 1L;
    private static final String TEAM_NAME = "test";
    private static final String TEAM_SPORT = "Hockey";

    @BeforeEach
    public void beforeAll() throws IOException {
        Location teamLocation = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", teamLocation);
        Location userLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", userLocation);
                
        team = Mockito.spy(team);
        user = Mockito.spy(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Test
    public void testGettingTeamList() throws Exception {
        Mockito.when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();

        mockMvc.perform(get("/profile")
                .param("teamID", TEAM_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("profileForm"))
            .andExpect(MockMvcResultMatchers.model().attribute("teamID", TEAM_ID))
            .andExpect(MockMvcResultMatchers.model().attribute("displayName", TEAM_NAME))
            .andExpect(MockMvcResultMatchers.model().attribute("displaySport", TEAM_SPORT))
            .andExpect(MockMvcResultMatchers.model().attribute("displayTeamPicture", team.getPictureString()));
    }

    @Test
    public void testGettingInvalidTeam() throws Exception {
        Mockito.when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();

        mockMvc.perform(get("/profile")
                        .param("teamID", "2"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"));
    }

    @Test
    public void testUploadValidProfilePicture() throws Exception {
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input.readAllBytes());
            mockMvc.perform(multipart("/profile")
                        .file(multipartFile)
                        .param("teamID", TEAM_ID.toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(String.format("/profile?teamID=%s", TEAM_ID)));
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
            mockMvc.perform(multipart("/profile?teamID={id}", TEAM_ID).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }
        assertNotEquals(mockTeamService.getProfilePictureEncodedString(team.getTeamId()), Base64.getEncoder().encodeToString(fileBytes));

    }

    @Test
    public void testUploadInvalidProfilePictureSize() throws Exception {
        Resource resource = new ClassPathResource("/testingfiles/maxFileSize.png");
        File file = resource.getFile();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png",
                    input.readAllBytes());
            mockMvc.perform(multipart("/profile?teamID={id}", TEAM_ID).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }
        assertNotEquals(mockTeamService.getProfilePictureEncodedString(team.getTeamId()), Base64.getEncoder().encodeToString(fileBytes));
    }

}
