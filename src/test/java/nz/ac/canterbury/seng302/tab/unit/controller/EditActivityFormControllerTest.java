package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser
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

    private static final String ACTVITY_ADDRESS_LINE_1 = "1 Memorial Road";
    private static final String ACTVITY_ADDRESS_LINE_2 = "A";
    private static final String ACTVITY_SUBURB = "Ilam";
    private static final String ACTVITY_POSTCODE = "8088";
    private static final String ACTVITY_CITY = "Rolleston";
    private static final String ACTVITY_COUNTRY = "New Zealand";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;
    @MockBean
    private TeamService mockTeamService;
    @MockBean
    private ActivityService mockActivityService;

    private Team team;

    private Activity activity;

    private static final Long TEAM_ID = 1L;

    final long ACT_ID = 99;

    @BeforeEach
    void beforeEach() throws IOException {
        Date userDOB;
        try {
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY,
                USER_POSTCODE, USER_COUNTRY);
        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        team = new Team("test", "Hockey", testLocation, testUser);
        LocalDateTime start =   LocalDateTime.of(2023, 6,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 7,1,8,30);
        Location activityLocation = new Location(ACTVITY_ADDRESS_LINE_1, ACTVITY_ADDRESS_LINE_2, ACTVITY_SUBURB,
                ACTVITY_CITY, ACTVITY_POSTCODE, ACTVITY_COUNTRY);
        activity= new Activity(Activity.ActivityType.Game,team, "testing the description",start,end,testUser, activityLocation);

        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockTeamService.findTeamsWithUser(testUser)).thenReturn(List.of(team));
    }

    @Test
    public void testDisplayingEditActivityReturns200() throws Exception {
        mockMvc.perform(get("/createActivity?edit={id}",activity.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivity"));
    }

    @Test
    public void whenAllFieldsAreValidReturn302() throws Exception {
        // We mock these validation methods because we're not testing them.
        // They should have their own tests, we only care about their output.
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        // When complete, the controller saves the activity & redirects to its ID.
        Activity activity = mock(Activity.class);
        when(activity.getId()).thenReturn(ACT_ID);
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(activity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activities"));
    }

    @Test
    public void whenDescriptionIsEmptyReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenStartDateTimeIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(false);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenEndDateTimeIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(false);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenTeamIsInvalidReturn400() throws Exception {
        final Long INVALID_TEAM_ID = 0L;
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockTeamService.getTeam(INVALID_TEAM_ID)).thenReturn(null);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", INVALID_TEAM_ID.toString())
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAddressLine1IsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "&&&&&&&")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAddressLine2IsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1b Show Place")
                        .param("addressLine2", "*****")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCityIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "$place$")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCountryIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "%%place%%%")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostCodeIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "smething")
                        .param("postcode", "&&)()()")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenSuburbIsInvalidReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "smething")
                        .param("postcode", "uwu")
                        .param("suburb", "^%^%^%"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCityIsEmptyReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "")
                        .param("country", "smething")
                        .param("postcode", "uwu")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCountryIsEmptyReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Gore")
                        .param("country", "")
                        .param("postcode", "uwu")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest());
    }

    /**
     * CREATE TESTS BELOW
     */

    @Test
    public void testDisplayingCreateActivityReturns200() throws Exception {
        mockMvc.perform(get("/createActivity"))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivity"));
    }

    @Test
    public void whenAllFieldsAreValidCreateActivity_Return302() throws Exception {
        // We mock these validation methods because we're not testing them.
        // They should have their own tests, we only care about their output.
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        // When complete, the controller saves the activity & redirects to its ID.
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(activity);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activities"));
    }

    @Test
    public void whenDescriptionIsEmptyCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenStartDateTimeIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(false);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenEndDateTimeIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(false);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenTeamIsInvalidCreateActivity_Return400() throws Exception {
        final Long INVALID_TEAM_ID = 0L;
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockTeamService.getTeam(INVALID_TEAM_ID)).thenReturn(null);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", INVALID_TEAM_ID.toString())
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAddressLine1IsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "&&&&&&&")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAddressLine2IsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1b Show Place")
                        .param("addressLine2", "*****")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCityIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "$place$")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCountryIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "%%place%%%")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostCodeIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "smething")
                        .param("postcode", "&&)()()")
                        .param("suburb", "A Place"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenSuburbIsInvalidCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Hamilton")
                        .param("country", "smething")
                        .param("postcode", "uwu")
                        .param("suburb", "^%^%^%"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCityIsEmptyRCreateActivity_eturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "")
                        .param("country", "smething")
                        .param("postcode", "uwu")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCountryIsEmptyCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/createActivity")
                        .param("activityType", String.valueOf(Activity.ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-01-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "6H Place")
                        .param("addressLine2", "B")
                        .param("city", "Gore")
                        .param("country", "")
                        .param("postcode", "uwu")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest());
    }
}