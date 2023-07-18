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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewClubControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    TeamRepository teamRepository;

    @MockBean
    private UserService mockUserService;
    @MockBean
    private ClubService mockClubService;

    private Team team;

    private Club club;

    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "Chch",
                null, "NZ");
        User user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team 900", "Rugby");
        userRepository.save(user);

        club = new Club("Rugby Club", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), "Rugby");
        team.setTeamClub(club);

        clubRepository.save(club);
        teamRepository.save(team);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        Mockito.when(mockClubService.findClubById(club.getClubId())).thenReturn(Optional.of(club));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        teamRepository.deleteAll();
        clubRepository.deleteAll();
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testGettingViewClubPageOfValidClub() throws Exception {
        mockMvc.perform(get("/view-club?clubID={id}",club.getClubId()))
                .andExpect(status().isOk())
                .andExpect(view().name("viewClub"))
                .andExpect(MockMvcResultMatchers.model().attribute("club", club));

    }


    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testGettingViewClubPageOfInvalidClub() throws Exception {
        mockMvc.perform(get("/view-club?clubID={id}",-1))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"));
    }
}
