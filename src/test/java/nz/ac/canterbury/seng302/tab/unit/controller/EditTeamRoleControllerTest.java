package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EditTeamRoleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TeamService mockTeamService;

    @MockBean UserService mockUserService;

    User user;

    private static Long TEAM_ID = 1L;
    private static Long NEWTEAM_ID = 2L;
    private static Long USER_ID = 3L;

    private static final String MANAGER = Role.MANAGER.toString();

    @BeforeEach
    public void setUp() throws IOException {
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        user = spy(user);
        Mockito.when(user.getUserId()).thenReturn(USER_ID);

        //Mock User
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        //Generic Team for testing
        Team team = new Team("team900", "programming");
        team.setManager(mockUserService.getCurrentUser().get());
        team.setTeamId(TEAM_ID);
        team = spy(team);
        Mockito.when(team.getTeamId()).thenReturn(TEAM_ID);
        Mockito.when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);

        //Team for test where user isn't the manager
        Team newTeam = new Team("Better Team 900", "Write Integration Tests");
        newTeam = spy(newTeam);
        Mockito.when(newTeam.getTeamId()).thenReturn(NEWTEAM_ID);
        Mockito.when(mockTeamService.getTeam(NEWTEAM_ID)).thenReturn(newTeam);
    }

    @Test
    public void getRequest_withValidTeamID() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                .param("edit", String.valueOf(TEAM_ID))).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"));
    }

    @Test
    public void getRequest_withInValidTeamID() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                        .param("edit", String.valueOf(-1))).andExpect(status().isFound())
                .andExpect((redirectedUrl("/home")));
    }


    @Test
    public void getRequest_whenNotManagerOfTeam() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                        .param("edit", NEWTEAM_ID.toString())).andExpect(status().isFound())
                .andExpect((redirectedUrl("/home")));
    }

    @Test
    public void getRequest_badRequestNoTeamIDProvided() throws Exception {
        mockMvc.perform(get("/editTeamRole"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getRequest_invalidAsNoUserProvided() throws Exception {
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.empty());
        mockMvc.perform(get("/editTeamRole")
                        .param("edit", NEWTEAM_ID.toString()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void postRequest_ValidDetails() throws Exception {
        Mockito.when(mockTeamService.userRolesAreValid(List.of(MANAGER))).thenReturn(true);
        mockMvc.perform(post("/editTeamRole")
                        .param("teamID", TEAM_ID.toString())
                        .param("userRoles", MANAGER)
                        .param("userIds", USER_ID.toString()))
                .andExpect(status().isOk());

        verify(mockTeamService, Mockito.times(1)).updateTeam(any());
    }

    @Test
    public void postRequest_TooFewManagers() throws Exception {
        Mockito.when(mockTeamService.userRolesAreValid(List.of())).thenReturn(false);
        mockMvc.perform(post("/editTeamRole")
                        .param("teamID", TEAM_ID.toString())
                        .param("userRoles", MANAGER)
                        .param("userIds", USER_ID.toString()))
                .andExpect(status().isOk());

        verify(mockTeamService, Mockito.times(0)).updateTeam(any());
    }

    @Test
    public void postRequest_TooManyManagers() throws Exception {
        Mockito.when(mockTeamService.userRolesAreValid(any())).thenReturn(false);
        mockMvc.perform(post("/editTeamRole")
                        .param("teamID", TEAM_ID.toString())
                        .param("userRoles", MANAGER, MANAGER, MANAGER, MANAGER)
                        .param("userIds", USER_ID.toString(), "4","5","6"))
                .andExpect(status().isOk());

        verify(mockTeamService, Mockito.times(0)).updateTeam(any());
    }

    @Test
    public void postRequest_withInValidTeamID() throws Exception {
        mockMvc.perform(post("/editTeamRole")
                        .param("teamID", "-1")
                        .param("userRoles", MANAGER)
                        .param("userIds", String.valueOf(USER_ID.toString())))
                .andExpect(status().isFound());
    }


    @Test
    public void postRequest_whenNotManagerOfTeam() throws Exception {
        mockMvc.perform(post("/editTeamRole")
                        .param("teamID", NEWTEAM_ID.toString())
                        .param("userRoles", MANAGER)
                        .param("userIds", USER_ID.toString()))
                .andExpect(status().isFound());
    }
}
