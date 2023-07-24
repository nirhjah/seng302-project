package nz.ac.canterbury.seng302.tab.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * Unfortunately, due to non-stop issues around
 * <code>org.hibernate.PersistentObjectException: detached entity passed to persist:</code>,
 * whenever I try to save anything to the database, these tests use mocking.
 */
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U33EditLineupForGameFeature {

    @Autowired
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultActions response;

    private TeamService teamService;
    private UserService userService;
    private ActivityService activityService;
    private FormationService formationService;

    private User user;
    private Team team;
    private Activity activity;

    private static final Long ACT_ID = 999L;

    private Map<String, Formation> formationMap = new HashMap<>();
    private long formationId = 0;

    private void setupMorganMocking() {
        // get all the necessary beans
        userService = Mockito.spy(applicationContext.getBean(UserService.class));
        teamService = Mockito.spy(applicationContext.getBean(TeamService.class));
        activityService = Mockito.spy(applicationContext.getBean(ActivityService.class));
        formationService = Mockito.spy(applicationContext.getBean(FormationService.class));

        // create mockMvc manually with spied services
        var controller = new CreateActivityController(
                teamService, userService, activityService, formationService, null
        );
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Before("@edit_lineup_for_game")
    public void setup() throws Exception {
        setupMorganMocking();
        Location location = new Location("abcd", null, null, "chch", null, "nz");
        user = new User("Test", "User", "test@example.com", "insecure", location);
        user = userService.updateOrAddUser(user);
        user = userService.findUserById(user.getUserId()).orElseThrow();
        Mockito.doReturn(Optional.of(user)).when(userService).getCurrentUser();
    }

    @Given("I am the manager of a team")
    public void i_am_the_manager_of_a_team() throws IOException {
        Location location = new Location("42 Wallaby Way", null, null, "Sydney", null, "Australia");
        team = new Team("Test Team", "Fire Juggling", location);
        team = teamService.addTeam(team);

        team.setManager(user);

        team = teamService.updateTeam(team);
    }

    @Given("the team has a formation {string}")
    public void the_team_has_a_formation(String formationStr) {
        // Problem: The webpage sends the ID of the formation, so
        // we need to remember it for later steps.
        // We're also mocking here because detached entities make me cry
        Formation formation = Mockito.spy(new Formation(formationStr, team));
        Mockito.doReturn(formationId).when(formation).getFormationId();
        Mockito.doReturn(Optional.of(formation)).when(formationService).findFormationById(formationId);
        formationId += 1;
        formationMap.put(formation.getFormation(), formation);
    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() {
        // Filling out the unimportant required fields...
        requestBuilder = post("/createActivity")
                .param("team", String.valueOf(team.getTeamId()))
                .param("description", "testing edit description")
                .param("startDateTime", "3023-07-01T10:00:00")
                .param("endDateTime", "3023-08-01T12:00:00")
                .param("addressLine1", "43 Wallaby Way")
                .param("city", "Greymouth")
                .param("country", "New Zealand")
                .param("postcode", "1234");
    }

    @Given("the activity has type game or friendly")
    public void the_activity_has_type_game_or_friendly() {
        Location location = new Location("efgh", null, null, "chch", null, "nz");

        LocalDateTime startDate = LocalDateTime.of(3023, 1, 1, 1, 1);
        LocalDateTime endDate = LocalDateTime.of(3023, 2, 1, 1, 1);
        user = userService.findUserById(user.getUserId()).orElseThrow();
        activity = Mockito.spy(
                new Activity(ActivityType.Game, team, "testing edit description", startDate, endDate, user, location));
        // We have to mock, otherwise we get detached entity errors, even though
        // the entities are saved, and even if we immediately query for them back.
        // I know this is wrong, but I've been at this for so long.
        Mockito.doReturn(ACT_ID).when(activity).getId();
        Mockito.doReturn(activity).when(activityService).findActivityById(ACT_ID);
        Mockito.doReturn(activity).when(activityService).updateOrAddActivity(activity);

        requestBuilder = requestBuilder
                .param("activityType", activity.getActivityType().toString())
                .param("actId", String.valueOf(ACT_ID));
    }

    @When("I select the line-up {string} from the list of existing team formations")
    public void i_select_the_line_up_from_the_list_of_existing_team_formations(String formationStr) {
        // Problem: The webpage sends the ID of the formation,
        // luckily we saved that earlier
        requestBuilder = requestBuilder.param("formation",
                String.valueOf(formationMap.get(formationStr).getFormationId()));
    }

    @Then("the saved activity has the formation {string}")
    public void the_saved_activity_has_the_formation(String formationStr) throws Exception {
        Mockito.doReturn(List.copyOf(formationMap.values())).when(formationService).getTeamsFormations(team.getTeamId());

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/view-activity?activityID=" + ACT_ID))
                .andReturn();

        verify(activityService).updateOrAddActivity(activity);
        assertTrue(activity.getFormation().isPresent());
        assertEquals(formationStr, activity.getFormation().get().getFormation());
    }
}
