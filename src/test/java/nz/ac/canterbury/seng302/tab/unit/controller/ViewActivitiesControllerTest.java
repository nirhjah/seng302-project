package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
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
public class ViewActivitiesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    Activity game;

    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "Chch",
                null, "NZ");
        User user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);
        game = new Activity(ActivityType.Other, null , "Personal Activity",
                LocalDateTime.of(2030, 1,1,6,30),
                LocalDateTime.of(2030, 1,1,8,30), user,
                new Location(null,null,null,"CHCH", null, "NZ"));
        activityRepository.save(game);
    }
    @AfterEach
    public void afterEach() {
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }


    /**
     * Mock request for a user's activities
     * @throws Exception exception thrown when mockMVC is unable to carry out request
     */
    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewAllTeamActivities() throws Exception {
        mockMvc.perform(get("/view-activities").param("page", "1")).andExpect(status().isOk())
                .andExpect(view().name("viewActivities")).andExpect(model().attributeExists("activities"));
    }
}
