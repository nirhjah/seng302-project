package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EditActivityFormControllerTest {
    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_EMAIL = "test@email.org";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_PWORD = "super_insecure";
    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService mockUserService;

    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    private Activity activity;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void beforeEach() throws IOException {
        activityRepository.deleteAll();
        Date userDOB;
        try {
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);
        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        team = new Team("test", "Hockey", testLocation);
        LocalDateTime start =   LocalDateTime.of(2023, 6,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 7,1,8,30);
        activity= new Activity(Activity.ActivityType.Game,team, "testing the description",start,end,testUser);
//        activityRepository.save(activity);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
    }
    @Test
    public void testDisplayingEditActivityReturns200() throws Exception {
        mockMvc.perform(get("/createActivity?edit={id}",activity.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivity"));
    }
    @Test
    public void whenAllFieldsAreValidReturn302() throws Exception {
        mockMvc.perform(post("/createActivity?edit={id}", activity.getId())
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00"))
                .andExpect(status().isFound());
    }
    @Test
    public void whenStartDateTimeIsInvalidReturn400() throws Exception {
        mockMvc.perform(post("/createActivity?edit={id}",activity.getId())
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDescriptionIsEmptyReturn400(){}

    @Test
    public void whenEndDateTimeIsInvalidReturn400(){}
}