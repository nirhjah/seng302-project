package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.enums.ActivityOutcome;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FactService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser
public class ViewActivityControllerTests {
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
    private FactService mockFactService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    private Team team;

    @Autowired
    private TeamRepository teamRepository;

    private Activity activity;

    private User activityPlayer;

    private User activityPlayer2;

    private Activity otherActivity;



    @BeforeEach
    void beforeEach() throws IOException {
        userRepository.deleteAll();
        activityRepository.deleteAll();
        Date userDOB;
        try {
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY,
                USER_POSTCODE, USER_COUNTRY);
        Location testLocation2 = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY,
                USER_POSTCODE, USER_COUNTRY);
        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        activityPlayer = new User("Bob", "Smith", userDOB, "bob@gmail.com", USER_PWORD, testLocation2);
        activityPlayer2 = new User("joe", "Smith", userDOB, "bob@gmail.com", USER_PWORD, testLocation2);
        userRepository.save(activityPlayer);

        team = new Team("test", "Hockey", testLocation, testUser);
        LocalDateTime start =   LocalDateTime.of(2023, 6,1,6,30);
        LocalDateTime end = LocalDateTime.of(2025, 7,1,8,30);
        Location activityLocation = new Location(ACTVITY_ADDRESS_LINE_1, ACTVITY_ADDRESS_LINE_2, ACTVITY_SUBURB,
                ACTVITY_CITY, ACTVITY_POSTCODE, ACTVITY_COUNTRY);
        Location activityLocation1 = new Location(ACTVITY_ADDRESS_LINE_1, ACTVITY_ADDRESS_LINE_2, ACTVITY_SUBURB,
                ACTVITY_CITY, ACTVITY_POSTCODE, ACTVITY_COUNTRY);
        activity= new Activity(ActivityType.Game, team, "description",start, end, testUser, activityLocation);
        otherActivity= new Activity(ActivityType.Other, null, "description",start, end, null, activityLocation1);

        activityRepository.save(activity);
        activityRepository.save(otherActivity);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "1h 25m", activity));
        factList.add(new Fact("Someone fell over again", "1h 30m", activity));
        factList.add(new Fact("Someone fell over yet again", "1h 42m", activity));
        factList.add(new Fact("Testing scrollable feature", "1h 25m", activity));

        teamRepository.save(team);
        activityPlayer.joinTeam(team);
        activityPlayer2.joinTeam(team);

        when(mockActivityService.findActivityById(Long.parseLong("1"))).thenReturn(activity);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockTeamService.findTeamsWithUser(testUser)).thenReturn(List.of(team));
        when(mockFactService.getAllFactsForActivity(activity)).thenReturn(factList);

    }

    @AfterEach
    void afterEach() {
        activityRepository.deleteAll();
    }
    @Test
    void testGettingViewActivityPageOfValidActivity() throws Exception {
        mockMvc.perform(get("/view-activity?activityID={id}","1"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewActivity"))
                .andExpect(model().attribute("activity", activity));
    }

    @Test
    void testGettingViewActivityPageOfInvalidActivity() throws Exception {
        mockMvc.perform(get("/view-activity?activityID={id}",4))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddingActivityTeamGoalUseDefault() throws Exception {
        mockMvc.perform(post("/overall-score", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "1")
                        .param("overallScoreOpponent", "1")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(2)).validateActivityScore(any(), any());
    }

    @Test
    void testAddingOverallScoreOneScoreEmpty() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.validateActivityScore("", "5-6")).thenReturn(2);


        mockMvc.perform(post("/overall-score", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "")
                        .param("overallScoreOpponent", "5-6")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1")).andExpect(MockMvcResultMatchers.flash().attribute("scoreInvalid", "Leave Modal Open"));

    }

    @Test
    void testAddingOverallScoreBothDontMatch() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.validateActivityScore("3", "5-6")).thenReturn(1);


        mockMvc.perform(post("/overall-score", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "3")
                        .param("overallScoreOpponent", "5-6")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1")).andExpect(MockMvcResultMatchers.flash().attribute("scoreInvalid", "Leave Modal Open"));

    }

    @Test
    void testAddingOverallScoreBeforeActivityStart() throws Exception {
        LocalDateTime startLate =   LocalDateTime.of(2024, 6,1,6,30);

        activity.setActivityStart(startLate);
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        
        mockMvc.perform(post("/overall-score", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "3")
                        .param("overallScoreOpponent", "4")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1")).andExpect(MockMvcResultMatchers.flash().attribute("scoreInvalid", "Leave Modal Open"));

    }

    @Test
    void testAddingOverallScoreBothValid() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.validateActivityScore("3", "4")).thenReturn(0);

        mockMvc.perform(post("/overall-score", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "3")
                        .param("overallScoreOpponent", "4")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));

    }

    @Test
    void testAddingGoalValidFields() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.checkTimeOfFactWithinActivity(activity, 2)).thenReturn(true);
        when(mockUserService.findUserById(Long.parseLong("2"))).thenReturn(Optional.of(activityPlayer));

        mockMvc.perform(post("/add-goal", 42L)
                        .param("actId", "1")
                        .param("scorer", "2")
                        .param("goalValue", "1")
                        .param("description", "goal scored")
                        .param("time", "2")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1")).andExpect(status().isFound());

        verify(mockActivityService, times(1)).updateOrAddActivity(any());

    }


    @Test
    void testAddingGoal_timeBlank() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.checkTimeOfFactWithinActivity(activity, 2)).thenReturn(true);
        when(mockUserService.findUserById(Long.parseLong("2"))).thenReturn(Optional.of(activityPlayer));

        mockMvc.perform(post("/add-goal", 42L)
                        .param("actId", "1")
                        .param("scorer", "2")
                        .param("goalValue", "1")
                        .param("description", "goal scored")
                        .param("time", "")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));

        verify(mockActivityService, times(0)).updateOrAddActivity(any());

    }

    @Test
    void testAddingGoal_scorerBlank() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        when(mockActivityService.checkTimeOfFactWithinActivity(activity, 2)).thenReturn(true);
        when(mockUserService.findUserById(Long.parseLong("2"))).thenReturn(Optional.of(activityPlayer));

        mockMvc.perform(post("/add-goal", 42L)
                        .param("actId", "1")
                        .param("scorer", "1")
                        .param("goalValue", "1")
                        .param("description", "goal scored")
                        .param("time", "")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));

        verify(mockActivityService, times(0)).updateOrAddActivity(any());

    }

    @Test
    void testAddFactDescriptionAndTime() throws Exception {
        when(mockActivityService.findActivityById(otherActivity.getId())).thenReturn(otherActivity);

        mockMvc.perform(post("/add-fact", 42L)
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", "3")
                        .param("description", "4")
                )
                .andExpect(view().name("redirect:./view-activity?activityID="+otherActivity.getId()));
    }

    @Test
    void testAddFactNoDescriptionAndTime() throws Exception {
        when(mockActivityService.findActivityById(otherActivity.getId())).thenReturn(otherActivity);

        mockMvc.perform(post("/add-fact", 42L)
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", "3")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddFactDescriptionAndStringForTime() throws Exception {
        when(mockActivityService.findActivityById(otherActivity.getId())).thenReturn(otherActivity);

        mockMvc.perform(post("/add-fact", 42L)
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", "favour")
                        .param("description", "chchc")
                )
                .andExpect(status().isFound());
    }

    @Test
    void testAddFactDescriptionAndToolongTime() throws Exception {
        when(mockActivityService.findActivityById(otherActivity.getId())).thenReturn(otherActivity);

        mockMvc.perform(post("/add-fact", 42L)
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", "99999999999999")
                        .param("description", "chchc")
                )
                .andExpect(status().isFound());
    }

    @Test
    void testAddOutcomeSetAsWin() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(post("/add-outcome", 42L)
                        .param("actId", String.valueOf(activity.getId()))
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.Win))
                )
                .andExpect(status().isFound());
    }

    @Test
    void testAddOutcomeSetNothing() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(post("/add-outcome", 42L)
                        .param("actId", String.valueOf(activity.getId()))
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.None))
                )
                .andExpect(status().isFound());
    }

    @Test
    void testAddOutcomeSetAsDraw() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(post("/add-outcome", 42L)
                        .param("actId", String.valueOf(activity.getId()))
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.Draw))
                )
                .andExpect(status().isFound());
    }

    @Test
    void testAddOutcomeSetAsLoss() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(post("/add-outcome", 42L)
                        .param("actId", String.valueOf(activity.getId()))
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.Loss))
                )
                .andExpect(status().isFound());
    }

    @Test
    void testDescriptionLengthFact() throws Exception {
        String description = "a".repeat(151);
        mockMvc.perform(post("/add-fact", 42L)
                        .param("actId", "1")
                        .param("timeOfFact", "5")
                        .param("description", description))
                .andExpect(MockMvcResultMatchers.status().isFound());
    }

    @Test
    void testDescriptionLengthGoal() throws Exception {
        String description = "a".repeat(151);
        mockMvc.perform(post("/add-goal", 42L)
                        .param("actId", "1")
                        .param("scorer", "1")
                        .param("goalValue", "1")
                        .param("description", description)
                        .param("time", "")
                )
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();
    }

    @Test
    void testDescriptionLengthSub() throws Exception {
        String description = "a".repeat(151);
        String player1IDString = Long.toString(activityPlayer.getId());
        String player2IDString = Long.toString(activityPlayer2.getId());
        mockMvc.perform(post("/add-sub", 42L)
                        .param("actId", "1")
                        .param("playerOn", player1IDString)
                        .param("goalValue", player2IDString)
                        .param("description", description)
                        .param("time", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();
    }

}
