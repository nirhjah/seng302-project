package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.LineUpService;
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
    @MockBean
    private FormationService mockFormationService;

    @MockBean
    private LineUpService mockLineUpService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LineUpRepository lineUpRepository;
    private Team team;
    private User testUser;
    private Activity activity;

    private static final Long TEAM_ID = 1L;

    private static final long ACT_ID = 99;

    private static final Long FORMATION_ID = 123L;

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
        testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        team = spy(new Team("test", "Hockey", testLocation, testUser));
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();
        LocalDateTime start = LocalDateTime.of(2023, 6, 1, 6, 30);
        LocalDateTime end = LocalDateTime.of(2023, 7, 1, 8, 30);
        Location activityLocation = new Location(ACTVITY_ADDRESS_LINE_1, ACTVITY_ADDRESS_LINE_2, ACTVITY_SUBURB,
                ACTVITY_CITY, ACTVITY_POSTCODE, ACTVITY_COUNTRY);

        activity = new Activity(ActivityType.Game, team, "testing the description", start, end, testUser, activityLocation);
        when(mockActivityService.getAllTeamActivities(team)).thenReturn(List.of(activity));
        // mockActivityService.updateOrAddActivity(activity);

        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(team);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockTeamService.findTeamsWithUser(testUser)).thenReturn(List.of(team));
    }

    @Test
    public void testDisplayingEditActivityReturns200() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(get("/create-activity?edit={id}", activity.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivityForm"));
    }

    @Test
    public void whenAllFieldsAreValidReturn302() throws Exception {
        // We mock these validation methods because we're not testing them.
        // They should have their own tests, we only care about their output.
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        // When complete, the controller saves the activity & redirects to its ID.
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
                        .param("formation", "-1")
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
                .andExpect(redirectedUrl("./view-activity?activityID=" + localActivity.getId()));
    }

    @Test
    public void whenDescriptionIsEmptyReturn400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
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
        mockMvc.perform(get("/create-activity"))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivityForm"));
    }

    @Test
    public void whenAllFieldsAreValidCreateActivity_Return302() throws Exception {
        // We mock these validation methods because we're not testing them.
        // They should have their own tests, we only care about their output.
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);

        // When complete, the controller saves the activity & redirects to its ID.
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(activity);

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("formation", "-1")
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
                .andExpect(redirectedUrl("./view-activity?activityID=" + activity.getId()));
    }

    @Test
    public void whenDescriptionIsEmptyCreateActivity_Return400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
                        .param("team", String.valueOf(TEAM_ID))
                        .param("formation", "-1")
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

        mockMvc.perform(post("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Training))
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

//    @Test
//    public void whenValidFormationIsSpecified_editActivity_formationSaved() throws Exception {
//        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
//        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
//        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
//
//        Formation formation = new Formation("1-2-3", team);
//        when(mockFormationService.findFormationById(FORMATION_ID)).thenReturn(Optional.of(formation));
//        Activity localActivity = spy(activity);
//        Mockito.doReturn(ACT_ID).when(localActivity).getId();
//        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
//        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
//
//        mockMvc.perform(post("/create-activity")
//                        .param("actId", String.valueOf(ACT_ID))
//                        .param("activityType", String.valueOf(ActivityType.Game))
//                        .param("formation", String.valueOf(FORMATION_ID))
//                        .param("team", String.valueOf(TEAM_ID))
//                        .param("description", "testing edit description")
//                        .param("startDateTime", "2023-07-01T10:00:00")
//                        .param("endDateTime", "2023-08-01T12:00:00")
//                        .param("addressLine1", "1 Change address")
//                        .param("addressLine2", "B")
//                        .param("city", "Greymouth")
//                        .param("country", "New Zealand")
//                        .param("postcode", "8888")
//                        .param("suburb", "A Place"))
//                .andExpect(status().isFound())
//                .andExpect(redirectedUrl("./view-activity?activityID=" + ACT_ID));
//
//        Mockito.verify(localActivity).setFormation(formation);
//
//    }

    @Test
    void whenValidFormationIsSpecified_createActivity_formationSaved() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);

        Formation formation = new Formation("1-2-3", team);
        when(mockFormationService.findFormationById(FORMATION_ID)).thenReturn(Optional.of(formation));
        Activity activity = Mockito.mock(Activity.class);
        Mockito.doReturn(ACT_ID).when(activity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(activity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(null);

        mockMvc.perform(post("/create-activity")
                        .param("actId", "-1")
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", String.valueOf(FORMATION_ID))
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
                .andExpect(redirectedUrl("./view-activity?activityID=" + ACT_ID));

        ArgumentCaptor<Activity> argumentCaptor = ArgumentCaptor.forClass(Activity.class);
        Mockito.verify(mockActivityService).updateOrAddActivity(argumentCaptor.capture());

        assertEquals(formation, argumentCaptor.getValue().getFormation().orElse(null));

    }

    @Test
    public void whenFormationWithoutTeam_formationNotSaved() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);

        Formation formation = new Formation("1-2-3", team);
        when(mockFormationService.findFormationById(FORMATION_ID)).thenReturn(Optional.of(formation));
        Activity activity = Mockito.mock(Activity.class);
        Mockito.doReturn(ACT_ID).when(activity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(activity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(activity);

        mockMvc.perform(post("/create-activity")
                .param("actId", String.valueOf(ACT_ID))
                .param("activityType", String.valueOf(ActivityType.Game))
                .param("formation", String.valueOf(FORMATION_ID))
                .param("team", "-1")
                .param("description", "testing edit description")
                .param("startDateTime", "2023-07-01T10:00:00")
                .param("endDateTime", "2023-08-01T12:00:00")
                .param("addressLine1", "1 Change address")
                .param("addressLine2", "B")
                .param("city", "Greymouth")
                .param("country", "New Zealand")
                .param("postcode", "8888")
                .param("suburb", "A Place"));

        Mockito.verify(activity).setFormation(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Other", "Training", "Competition"})
    public void whenFormationButActivityTypeIsUnsupported_formationNotSaved(String activityType) throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);

        Formation formation = new Formation("1-2-3", team);
        when(mockFormationService.findFormationById(FORMATION_ID)).thenReturn(Optional.of(formation));
        Activity thisActivity = Mockito.spy(activity);
        Mockito.doReturn(ACT_ID).when(thisActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(thisActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(thisActivity);

        mockMvc.perform(post("/create-activity")
                .param("actId", String.valueOf(ACT_ID))
                .param("activityType", activityType)
                .param("formation", String.valueOf(FORMATION_ID))
                .param("team", String.valueOf(TEAM_ID))
                .param("description", "testing edit description")
                .param("startDateTime", "2023-07-01T10:00:00")
                .param("endDateTime", "2023-08-01T12:00:00")
                .param("addressLine1", "1 Change address")
                .param("addressLine2", "B")
                .param("city", "Greymouth")
                .param("country", "New Zealand")
                .param("postcode", "8888")
                .param("suburb", "A Place"));

        Mockito.verify(thisActivity).setFormation(null);
    }

    @Test
    public void whenFormationPointsToNothing_Returns400() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);

        Formation formation = new Formation("1-2-3", team);
        when(mockFormationService.findFormationById(FORMATION_ID)).thenReturn(Optional.of(formation));
        Activity thisActivity = Mockito.spy(activity);
        Mockito.doReturn(ACT_ID).when(thisActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(thisActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(thisActivity);

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", "888888")
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
                .andExpect(status().isBadRequest());
    }
    @Test
    public void whenAskingForTeamFormation_isManager_succeed() throws Exception {
        Formation formation1 = Mockito.spy(new Formation("1-2-3", team));
        Formation formation2 = Mockito.spy(new Formation("2-3-4", team));
        Mockito.doReturn(1L).when(formation1).getFormationId();
        Mockito.doReturn(2L).when(formation2).getFormationId();
        when(mockFormationService.getTeamsFormations(TEAM_ID)).thenReturn(
                List.of(formation1, formation2)
        );

        String EXPECTED_JSON = """
                    {"1":"1-2-3","2":"2-3-4"}
                """;

        mockMvc.perform(get("/create-activity/get_team_formation")
                        .param("teamId", String.valueOf(TEAM_ID)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenAskingForTeamFormation_isCoach_succeed() throws Exception {
        Formation formation1 = Mockito.spy(new Formation("1-2-3", team));
        Formation formation2 = Mockito.spy(new Formation("2-3-4", team));
        Mockito.doReturn(1L).when(formation1).getFormationId();
        Mockito.doReturn(2L).when(formation2).getFormationId();
        when(mockFormationService.getTeamsFormations(TEAM_ID)).thenReturn(
                List.of(formation1, formation2)
        );

        team.setCoach(testUser);

        String EXPECTED_JSON = """
                    {"1":"1-2-3","2":"2-3-4"}
                """;

        mockMvc.perform(get("/create-activity/get_team_formation")
                        .param("teamId", String.valueOf(TEAM_ID)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenAskingForTeamFormation_notInTeam_returnForbidden() throws Exception {
        // We're not in this team
        Team otherTeam = new Team("Wrong", "Sport");
        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(otherTeam);


        mockMvc.perform(get("/create-activity/get_team_formation")
                        .param("teamId", String.valueOf(TEAM_ID)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    public void whenAskingForTeamFormation_notTeamManagerOrCoach_returnForbidden() throws Exception {
        // We're not the manager of this team
        Team otherTeam = new Team("Wrong", "Sport");
        otherTeam.setMember(testUser);
        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(otherTeam);


        mockMvc.perform(get("/create-activity/get_team_formation")
                        .param("teamId", String.valueOf(TEAM_ID)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }


    @Test
    void editingLineUpWithValidLineUpAndSubs() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", "-1")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place")
                        .param("subs", "1")
                        .param("playerAndPositions", String.valueOf(List.of("1 1"))))

                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activity?activityID=" + localActivity.getId()));
        verify(mockActivityService, times(1)).updateOrAddActivity(any());

    }

    @Test
    void editingLineUpWithInvalidLineUp() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", "-1")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place")
                        .param("subs", "1")
                        .param("playerAndPositions", String.valueOf(List.of("1 X"))))

                .andExpect(status().isFound());
        verify(mockLineUpService, times(0)).updateOrAddLineUp(any());

    }


    /**
     * These tests make sure the acceptable inputs line up with U27's ACs.
     * Specifically AC7's "A description made of numbers or non-alphabetical characters only [is invalid]"
     */
    @ParameterizedTest
    @ValueSource(strings = {"Hello", "What's up?", "こんにちは", "haere rā", "Say hi\n\nSay bye", "We are going to 🏈 WIN", "4L"})
    void activityDescription_validDescriptions(String desc) throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        // When complete, the controller saves the activity & redirects to its ID.
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
                        .param("formation", "-1")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", desc)
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activity?activityID=" + localActivity.getId()));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"45", "💪💪💪", "1\n2\n3", ";~;"})
    void activityDescription_invalidDescriptions(String desc) throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        // When complete, the controller saves the activity & redirects to its ID.
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);
        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Training))
                        .param("formation", "-1")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", desc)
                        .param("startDateTime", "2023-07-01T10:00:00")
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
    void editingLineUpWithLineUpName() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);

        Formation formation = new Formation("1-1", team);
        when(mockFormationService.findFormationById(1)).thenReturn(Optional.of(formation));

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", "1")
                        .param("lineUpName", "testlineup1")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place")
                        .param("subs", "1")
                        .param("playerAndPositions", String.valueOf(List.of("1 1"))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activity?activityID=" + localActivity.getId()));
        verify(mockActivityService, times(1)).updateOrAddActivity(any());

        // Validate the name is saved
        ArgumentCaptor<LineUp> argumentCaptor = ArgumentCaptor.forClass(LineUp.class);
        verify(mockLineUpService).updateOrAddLineUp(argumentCaptor.capture());
        assertEquals("testlineup1", argumentCaptor.getValue().getLineUpName());

    }

    @Test
    void editingLineUpWithNoLineUpNameProvided() throws Exception {
        when(mockActivityService.validateStartAndEnd(any(), any())).thenReturn(true);
        when(mockActivityService.validateActivityDateTime(any(), any(), any())).thenReturn(true);
        when(mockActivityService.validateTeamSelection(any(), any())).thenReturn(true);
        Activity localActivity = spy(activity);
        Mockito.doReturn(ACT_ID).when(localActivity).getId();
        when(mockActivityService.updateOrAddActivity(any())).thenReturn(localActivity);
        when(mockActivityService.findActivityById(ACT_ID)).thenReturn(localActivity);

        Formation formation = new Formation("1-1", team);
        when(mockFormationService.findFormationById(1)).thenReturn(Optional.of(formation));

        mockMvc.perform(post("/create-activity")
                        .param("actId", String.valueOf(ACT_ID))
                        .param("activityType", String.valueOf(ActivityType.Game))
                        .param("formation", "1")
                        .param("lineUpName", "")
                        .param("team", String.valueOf(TEAM_ID))
                        .param("description", "testing edit description")
                        .param("startDateTime", "2023-07-01T10:00:00")
                        .param("endDateTime", "2023-08-01T12:00:00")
                        .param("addressLine1", "1 Change address")
                        .param("addressLine2", "B")
                        .param("city", "Greymouth")
                        .param("country", "New Zealand")
                        .param("postcode", "8888")
                        .param("suburb", "A Place")
                        .param("subs", "1")
                        .param("playerAndPositions", String.valueOf(List.of("1 1"))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("./view-activity?activityID=" + localActivity.getId()));
        verify(mockActivityService, times(1)).updateOrAddActivity(any());

        // Validate the name is saved
        ArgumentCaptor<LineUp> argumentCaptor = ArgumentCaptor.forClass(LineUp.class);
        verify(mockLineUpService).updateOrAddLineUp(argumentCaptor.capture());
        assertEquals("01/07/23 - 01/08/23: 1-1", argumentCaptor.getValue().getLineUpName());
    }


}
