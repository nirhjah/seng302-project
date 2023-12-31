package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
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
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewTeamActivitiesControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    TeamRepository teamRepository;

    Activity game;

    Team team;

    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "Chch",
                null, "NZ");
        User user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team 900", "Hockey");
        game = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2030, 1,1,6,30),
                LocalDateTime.of(2030, 1,1,8,30), user,
                new Location(null,null,null,"CHCH", null, "NZ"));
        activityRepository.save(game);
        teamRepository.save(team);
        userRepository.save(user);
    }
    @AfterEach
    public void afterEach() {
        activityRepository.deleteAll();
        userRepository.deleteAll();
        teamRepository.deleteAll();
    }


    /**
     * Mock request for team with activities
     * @throws Exception exception thrown when mockMVC is unable to carry out request
     */
    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewAllTeamActivities() throws Exception {
        mockMvc.perform(get("/viewTeamActivities").param("teamID", String.valueOf(team.getTeamId())).param("page", "1")).andExpect(status().isOk())
                .andExpect(view().name("viewActivities")).andExpect(model().attributeExists("activities"));
    }

    /**
     * Tests controller handles case with no team gracefully, by redirecting to the home page.
     * @throws Exception exception thrown when mockMVC is unable to carry out request
     */
    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewAllTeamActivities_NoTeam() throws Exception {
        mockMvc.perform(get("/viewTeamActivities").param("teamID", String.valueOf(-1)).param("page", "1")).andExpect(status().isFound())
                .andExpect((redirectedUrl("./home")));
    }
}
