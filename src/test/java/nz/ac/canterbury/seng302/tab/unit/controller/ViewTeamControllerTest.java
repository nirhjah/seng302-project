package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.image.TeamImageService;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class ViewTeamControllerTest {

    @Autowired
    private TeamImageService teamImageService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private FormationService mockFormationService;

    @MockBean
    private TeamService mockTeamService;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private FactService factService;

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
        
        team.setCoach(user);

        team = Mockito.spy(team);
        user = Mockito.spy(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Test
    public void testGettingTeamList() throws Exception {
        Mockito.when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();
        Mockito.when(activityService.getNumberOfWins(team)).thenReturn(0);
        Mockito.when(activityService.getNumberOfDraws(team)).thenReturn(0);
        Mockito.when(activityService.getNumberOfLoses(team)).thenReturn(0);
        Mockito.when(activityService.numberOfTotalGamesAndFriendlies(team)).thenReturn(0);
        Mockito.when(activityService.getLast5GamesOrFriendliesForTeamWithOutcome(team)).thenReturn(null);
        Mockito.when(factService.getTop5Scorers(team)).thenReturn(null);
        mockMvc.perform(get("/team-info")
                .param("teamID", TEAM_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("viewTeamForm"))
            .andExpect(MockMvcResultMatchers.model().attribute("teamID", TEAM_ID))
            .andExpect(MockMvcResultMatchers.model().attribute("displayName", TEAM_NAME))
            .andExpect(MockMvcResultMatchers.model().attribute("displaySport", TEAM_SPORT));
    }

    @Test
    public void testGettingInvalidTeam() throws Exception {
        Mockito.when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();

        mockMvc.perform(get("/team-info")
                        .param("teamID", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadValidProfilePicture() throws Exception {
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input.readAllBytes());
            mockMvc.perform(multipart("/team-info")
                        .file(multipartFile)
                        .param("teamID", TEAM_ID.toString()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(String.format("/team-info?teamID=%s", TEAM_ID)));
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
            mockMvc.perform(multipart("/team-info?teamID={id}", TEAM_ID).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }

        byte[] savedBytes = teamImageService.readFileOrDefault(team.getTeamId());
        assertFalse(Arrays.equals(savedBytes, fileBytes));
    }

    @Test
    public void testUploadInvalidProfilePictureSize() throws Exception {
        Resource resource = new ClassPathResource("/testingfiles/maxFileSize.png");
        File file = resource.getFile();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png",
                    input.readAllBytes());
            mockMvc.perform(multipart("/team-info?teamID={id}", TEAM_ID).file(multipartFile))
                    .andExpect(status().is3xxRedirection());
        }

        var savedBytes = teamImageService.readFileOrDefault(team.getTeamId());
        assertFalse(Arrays.equals(savedBytes, fileBytes));
    }

    @Test
    public void testCreatingAValidFormation() throws Exception {
        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        mockMvc.perform(post("/team-info/create-formation")
                        .param("formation", "1-4-4-2")
                        .param("customPlayerPositions", "")
                        .param("custom", String.valueOf(false))
                        .param("teamID", String.valueOf(TEAM_ID)))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/team-info?teamID=" + TEAM_ID));
        verify(mockFormationService, times(1)).addOrUpdateFormation(any());
    }

}
