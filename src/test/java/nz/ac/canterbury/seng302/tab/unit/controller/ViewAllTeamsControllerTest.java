package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAllTeamsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeamService teamService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "chch", null, "nz");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);
    }

    @AfterEach
    public void afterEach() throws IOException {
        userRepository.deleteAll();
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewTeamsWithATeam() throws Exception {
        Team team = new Team("Team 900", "Programming", new Location(null, null, null, "aud", null, "pls"));
        teamRepository.save(team);
        mockMvc.perform(get("/view-teams"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllTeams"));
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewTeamsWithNoTeams() throws Exception {
        mockMvc.perform(get("/view-teams"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home"));
    }
}
