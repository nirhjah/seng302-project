package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewClubControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    Team team;

    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "Chch",
                null, "NZ");
        User user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team 900", "Hockey");
        teamRepository.save(team);
        userRepository.save(user);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewAllTeamActivities() {

    }
}
