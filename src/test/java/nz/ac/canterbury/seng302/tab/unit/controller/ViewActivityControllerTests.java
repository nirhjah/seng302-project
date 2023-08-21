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
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
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

    private Team team;

    @Autowired
    private TeamRepository teamRepository;

    private Activity activity;



    @BeforeEach
    void beforeEach() throws IOException {
        activityRepository.deleteAll();
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
        activity= new Activity(ActivityType.Game, team, "description",start, end, testUser, activityLocation);

        activityRepository.save(activity);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "1h 25m", activity));
        factList.add(new Fact("Someone fell over again", "1h 30m", activity));
        factList.add(new Fact("Someone fell over yet again", "1h 42m", activity));
        factList.add(new Fact("Testing scrollable feature", "1h 25m", activity));

        teamRepository.save(team);
        when(mockActivityService.findActivityById(Long.parseLong("1"))).thenReturn(activity);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockTeamService.findTeamsWithUser(testUser)).thenReturn(List.of(team));
        when(mockFactService.getAllFactsForActivity(activity)).thenReturn(factList);

    }

    @AfterEach
    public void afterEach() {
        activityRepository.deleteAll();
    }
    @Test
    public void testGettingViewActivityPageOfValidActivity() throws Exception {
        mockMvc.perform(get("/view-activity?activityID={id}","1"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewActivity"))
                .andExpect(model().attribute("activity", activity));
    }

    @Test
    public void testGettingViewActivityPageOfInvalidActivity() throws Exception {
        mockMvc.perform(get("/view-activity?activityID={id}",4))
                .andExpect(status().isNotFound());
    }


    //Add Activity Stats tests

    @Test
    public void testAddingActivityFactWithNoDescription() throws Exception {
        mockMvc.perform(post("/view-activity", 42L)
                        .param("actId", "1")
                        .param("factType", String.valueOf(FactType.FACT))
                        .param("overallScoreTeam", "1")
                        .param("overallScoreOpponent", "1")
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.None))
                        .param("time", "1")
                        .param("goalValue", "1")
                        .param("scorer", "-1")
                        .param("playerOn", "1")
                        .param("playerOff", "1" )
                        .param("description", "")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(0)).updateOrAddActivity(any());

    }

    @Test
    public void testAddingActivityFactWithDescription() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);

        mockMvc.perform(post("/view-activity", 42L)
                        .param("actId", "1")
                        .param("factType", String.valueOf(FactType.FACT))
                        .param("time", "1")
                        .param("goalValue", "1")
                        .param("overallScoreTeam", "1")
                        .param("overallScoreOpponent", "1")
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.None))
                        .param("scorer", "-1")
                        .param("playerOn", "1")
                        .param("playerOff", "1" )
                        .param("description", "player fell over")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(1)).updateOrAddActivity(any());

    }

    @Test
    public void testAddingActivityTeamGoalUseDefault() throws Exception {
        mockMvc.perform(post("/overallScore", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "1")
                        .param("overallScoreOpponent", "1")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(2)).validateActivityScore(any(), any());
    }


    @Test
    public void testAddingSubstitutionFactWithSamePlayerOnPlayerOff() throws Exception {
        System.out.println(activity.getId());
        mockMvc.perform(post("/view-activity", 42L)
                        .param("actId", "1")
                        .param("factType", String.valueOf(FactType.SUBSTITUTION))
                        .param("overallScoreTeam", "1")
                        .param("overallScoreOpponent", "1")
                        .param("activityOutcomes", String.valueOf(ActivityOutcome.None))
                        .param("time", "1")
                        .param("goalValue", "")
                        .param("scorer", "-1")
                        .param("playerOn", "1")
                        .param("playerOff", "1" )
                        .param("description", "")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(0)).updateOrAddActivity(any());

    }

    @Test
    public void testAddingOverallScoreBothSameCorrectFormat() throws Exception {
        when(mockActivityService.findActivityById(activity.getId())).thenReturn(activity);

        mockMvc.perform(post("/overallScore", 42L)
                        .param("actId", "1")
                        .param("overallScoreTeam", "5-3")
                        .param("overallScoreOpponent", "2-6")
                )
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
        verify(mockActivityService, times(1)).updateOrAddActivity(any());
    }

}
