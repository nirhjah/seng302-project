package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class U27CreateActivityFeature {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService mockUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamService teamService;

    private User user;

    private String buildUrlEncodedFormEntity(String... params) {
        if( (params.length % 2) > 0 ) {
            throw new IllegalArgumentException("Need to give an even number of parameters");
        }
        StringBuilder result = new StringBuilder();
        for (int i=0; i<params.length; i+=2) {
            if( i > 0 ) {
                result.append('&');
            }
            try {
                result.
                        append(URLEncoder.encode(params[i], StandardCharsets.UTF_8.name())).
                        append('=').
                        append(URLEncoder.encode(params[i+1], StandardCharsets.UTF_8.name()));
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    @Given("I'm on the home page")
    @WithMockUser
    public void i_m_on_the_home_page() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk());
    }

    @And("There is a user called {string} {string}")
    @WithMockUser
    public void thereIsAUserCalled(String firstName, String lastName) {
        if (user == null) {
            user = new User(firstName, lastName, "test@test.com", "password1",
                    new Location(null, null, null, "chch", null, "NZ"));
        }
        Assertions.assertNotNull(user);
        Assertions.assertEquals(firstName, user.getFirstName());
        Assertions.assertEquals(lastName, user.getLastName());
    }

    @When("I click on create activity in the nav bar")
    @WithMockUser
    public void i_click_on_create_activity_in_the_nav_bar() throws Exception {
        mockMvc.perform(get("/createActivity"));
    }

    @Then("I'm taken to the create activity page")
    @WithMockUser
    public void i_m_taken_to_the_create_activity_page() throws Exception {
        mockMvc.perform(get("/createActivity")).andExpect(status().isFound());
    }

    @Given("I'm on the create activity page")
    @WithMockUser
    public void i_m_on_the_create_activity_page() throws Exception {
        mockMvc.perform(get("/createActivity")).andExpect(status().isFound());
    }

    @WithMockUser
    @When("I select {string} and {int}, and enter a valid description {string} and I select a valid {string} and {string} date time and press submit")
    public void i_select_game_and_and_enter_a_valid_description_game_with_team_and_i_select_a_valid_and_end_date_time_and_press_submit(String activityType, long teamId, String desc, String start, String end) throws Exception {
        CreateActivityForm createActivityForm = new CreateActivityForm();
        createActivityForm.setActivityType(Activity.ActivityType.valueOf(activityType));
        createActivityForm.setTeam(teamId);
        createActivityForm.setDescription(desc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        createActivityForm.setStartDateTime(LocalDateTime.parse(start, formatter));
        createActivityForm.setEndDateTime(LocalDateTime.parse(end, formatter));

        Team team = null;
        if (teamId != -1){
            team = new Team("TestName", "Sport");
            team = teamService.addTeam(team);
            teamId = team.getTeamId();
        }

        mockMvc.perform(post("/createActivity").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        "activityType", activityType,
                        "team", String.valueOf(teamId),
                        "description", desc,
                        "startDateTime", start,
                        "endDateTime", end
                ))).andExpect(status().isOk());
    }

    @Then("An activity is created")
    @WithMockUser
    public void an_activity_is_created() {
        List<Activity> acts = activityService.findAll();
        Assertions.assertTrue(acts.size() > 0);
    }

    @When("I select Other and {int}, and enter a valid description Meeting with team and I select a valid {int}-{int}-{int} {int}:{int} and <end> date time and press submit")
    @WithMockUser
    public void i_select_other_and_and_enter_a_valid_description_meeting_with_team_and_i_select_a_valid_and_end_date_time_and_press_submit(Integer int1, Integer int2, Integer int3, Integer int4, Integer int5, Integer int6) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I have at least {int} team I coach or manage")
    @WithMockUser
    public void i_have_at_least_team_i_coach_or_manage(Integer numTeams) throws IOException {
        Team t = new Team("Team Name", "Sport");
        teamService.addTeam(t);
        Assertions.assertTrue(teamService.getTeamList().size() >= numTeams);
    }

    @When("I select a team for the activity")
    public void i_select_a_team_for_the_activity() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I see all the teams I coach and manage and a no team option")
    public void i_see_all_the_teams_i_coach_and_manage_and_a_no_team_option() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select an activity type")
    public void i_select_an_activity_type() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I see activity type options {string}, {string}, {string}, {string}, {string}")
    public void i_see_activity_type_options(String string, String string2, String string3, String string4, String string5) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select {string} or {string} and I don't select a team")
    public void i_select_or_and_i_don_t_select_a_team(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I see an error message on the page")
    public void i_see_an_error_message_on_the_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
