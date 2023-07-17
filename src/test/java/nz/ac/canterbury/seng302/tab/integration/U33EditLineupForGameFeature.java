package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.Email;
import nz.ac.canterbury.seng302.tab.controller.RegisterController;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.mail.EmailDetails;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.utility.RegisterTestUtil;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private Map<String, Formation> formationMap = new HashMap<>();

    private void setupMorganMocking() {
        // get all the necessary beans
        userService = Mockito.spy(applicationContext.getBean(UserService.class));
        teamService = applicationContext.getBean(TeamService.class);
        activityService = applicationContext.getBean(ActivityService.class);
        formationService = applicationContext.getBean(FormationService.class);

        // create mockMvc manually with spied services
        var controller = new CreateActivityController(
                teamService, userService, activityService, formationService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Before("@edit_lineup_for_game")
    public void setup() throws Exception {
        setupMorganMocking();
        Location location = new Location("abcd", null, null, "chch", null, "nz");
        user = new User("Test", "User", "test@example.com", "insecure", location);
        user = userService.updateOrAddUser(user);
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
        // we need to remember it for later steps
        Team theTeam = teamService.getTeam(team.getTeamId()); // Detatched entity errors suck
        assertNotNull(theTeam);
        Formation formation = new Formation(formationStr, theTeam);
        formation = formationService.addOrUpdateFormation(formation);
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
        requestBuilder = requestBuilder.param("activityType", ActivityType.Game.toString());
    }

    @When("I select the line-up {string} from the list of existing team formations")
    public void i_select_the_line_up_from_the_list_of_existing_team_formations(String formationStr) {
        // Problem: The webpage sends the ID of the formation,
        // luckily we saved that earlier
        requestBuilder = requestBuilder.param("formation", String.valueOf(formationMap.get(formationStr).getFormationId()));
    }

    @Then("the saved activity has the formation {string}")
    public void the_saved_activity_has_the_formation(String formationStr) throws Exception {
        MvcResult result = mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("/view-activity*"))
            .andReturn();

        String url = result.getResponse().getRedirectedUrl();
        String activityIdStr = url.replaceAll("[^-?0-9]+", " ").split(" ")[0]; 
        
        activity = activityService.findActivityById(Long.valueOf(activityIdStr));
        assertTrue(activity.getFormation().isPresent());
        assertEquals(formationStr, activity.getFormation().get().getFormation());
    }
}
