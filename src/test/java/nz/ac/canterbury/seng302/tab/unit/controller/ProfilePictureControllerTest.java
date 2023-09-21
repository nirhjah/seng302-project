package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.image.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.image.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfilePictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserImageService userImageService;

    @MockBean
    private TeamImageService teamImageService;

    @MockBean
    private ClubImageService clubImageService;

    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClubService clubService;

    // Arbitrary bytes representing an image
    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};
    private final ResponseEntity<byte[]> response = ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(fileBytes);

    private static Team team;
    private static User user;
    private static Club club;

    @BeforeAll
    static void setupAll() throws Exception {
        user = User.defaultDummyUser();
        team = new Team("test", "Hockey");
        club = new Club("Rugby Club", null, "soccer",null);
    }

    @BeforeEach
    void setup() {
        userService.updateOrAddUser(user);
        teamService.updateTeam(team);
        clubService.updateOrAddClub(club);

        when(userImageService.getImageResponse(user.getUserId())).thenReturn(response);
        when(teamImageService.getImageResponse(team.getTeamId())).thenReturn(response);
        when(clubImageService.getImageResponse(club.getClubId())).thenReturn(response);
    }

    @Test
    void testUserProfilePicture() throws Exception {
        mockMvc.perform(get("/user-profile-picture/{id}", user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(this.fileBytes));
    }


    @Test
    void testClubLogo() throws Exception {
        mockMvc.perform(get("/club-logo/{id}", club.getClubId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(this.fileBytes))
                .andReturn();
    }

    @Test
    void testTeamProfilePicture() throws Exception {
        mockMvc.perform(get("/team-profile-picture/{id}", team.getTeamId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(this.fileBytes))
                .andReturn();
    }
}

