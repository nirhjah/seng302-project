package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ViewAllClubsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private ClubService mockClubService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ClubRepository clubRepository;

    private Team team;
    private User user;
    private Club club;

    private Club club2;

    private Club club3;

    @BeforeEach
    void beforeEach() throws IOException {
        team = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);


        club = new Club("Rugby Club", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), "Rugby",null);
        club2 = new Club("Hockey Club", new Location("34 Testing", "", "", "Nelson", "2020", "New Zealand"), "Hockey", null);
        club3 = new Club("Football Club", new Location("Testing", "", "", "Auckland", "2020", "New Zealand"), "Football", null);
        team.setTeamClub(club);

        clubRepository.save(club);
        teamRepository.save(team);
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @AfterEach
    void afterEach() {
        teamRepository.deleteAll();
        clubRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void viewAllClubsTest_noParams() throws Exception {
        mockMvc.perform(get("/view-clubs"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllClubs"));
    }

    @Test
    void viewAllClubsTest_withLocations() throws Exception {
        mockMvc.perform(get("/view-clubs").param("cities", "Christchurch"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllClubs"));
    }

    @Test
    void viewAllClubsTest_withSport() throws Exception {
        mockMvc.perform(get("/view-clubs").param("sports", "Rugby"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllClubs"));
    }

    @Test
    void viewAllClubsTest_withSearch() throws Exception {
        mockMvc.perform(get("/view-clubs").param("currentSearch", "Club"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllClubs"));
    }
}
