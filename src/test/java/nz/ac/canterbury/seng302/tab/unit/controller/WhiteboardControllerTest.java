package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WhiteboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;


    @MockBean
    private TeamService teamService;

    @MockBean
    private FormationService formationService;

    private Team team;

    private User user;
    private static final Long TEAM_ID = 1L;

    @SpyBean
    private UserService userService;

    @BeforeEach
    void beforeAll() throws IOException {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "chch", null, "nz");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);
        Location teamLocation = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", teamLocation);
        teamRepository.save(team);
        Formation formation = new Formation("1-2-3", team);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

        when(teamService.findTeamById(2L)).thenReturn(Optional.empty());
        when(teamService.findTeamById(TEAM_ID)).thenReturn(Optional.of(team));

        when(formationService.getTeamsFormations(TEAM_ID)).thenReturn(Collections.singletonList(formation));

    }

    @Test
    @WithMockUser
    void testGettingWhiteboardWithValidTeam() throws Exception {
        mockMvc.perform(get("/whiteboard")
                .param("teamID", TEAM_ID.toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("teamFormations"))
            .andExpect(model().attributeExists("teamLineUps"))
            .andExpect(model().attributeExists("teamMembers"));


    }

    @Test
    @WithMockUser
    void testGettingWhiteboardWithInvalidTeam() throws Exception {

        mockMvc.perform(get("/whiteboard")
                .param("teamID", "2"))
            .andExpect(status().isNotFound());
    }
}
