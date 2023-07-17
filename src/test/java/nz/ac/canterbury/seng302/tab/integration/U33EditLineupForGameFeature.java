package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.Email;
import nz.ac.canterbury.seng302.tab.controller.RegisterController;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U33EditLineupForGameFeature {

    @Autowired
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;

    private TeamService teamService;
    private UserService userService;
    private ActivityService activityService;
    private FormationService formationService;

    private User user;
    private Team team;
    private Activity activity;

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

        user = User.defaultDummyUser();
        Mockito.doReturn(Optional.of(user)).when(userService).getCurrentUser();
    }

    @Given("I am the manager of a team")
    public void i_am_the_manager_of_a_team() throws IOException {
        Location location = new Location("42 Wallaby Way", null, null, "Sydney", null, "Australia");
        Team team = new Team("Test Team", "Fire Juggling", location, user);
        team = teamService.addTeam(team);
    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() {
        // Blank step-def.
        // This test only exists so the test makes syntactical sense
    }

    @Given("the activity has type game or friendly")
    public void the_activity_has_type_game_or_friendly() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select a line-up from a list of existing team formations")
    public void i_select_a_line_up_from_a_list_of_existing_team_formations() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can add the selected line-up to the activity")
    public void i_can_add_the_selected_line_up_to_the_activity() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("the activity has type game or friendly and the has a selected formation")
    public void the_activity_has_type_game_or_friendly_and_the_has_a_selected_formation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select that formation for the game")
    public void i_select_that_formation_for_the_game() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the formation is displayed in the activity page")
    public void the_formation_is_displayed_in_the_activity_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
