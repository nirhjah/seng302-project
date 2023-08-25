package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.*;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class AddActivityStatisticsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;


    @Autowired
    private ApplicationContext applicationContext;


    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private FactService factService;


    private User user;

    private Team team;

    private Activity activity;

    private FactRepository factRespository;


    private ActivityRepository activityRepository;

    LocalDateTime startTime;



    @Before("@add_activity_stats")
    public void setup() throws IOException {

        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        factRespository = applicationContext.getBean(FactRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository));
        factService= Mockito.spy(new FactService(factRespository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivityController(userService,activityService,teamService,factService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);



        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        startTime =  LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime endTime = LocalDateTime.of(2023, 1,1,8,30);

        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("test1", "Hockey", new Location(null, null, null, "chch", null, "nz"), user);
        activity = new Activity(ActivityType.Game, null, "Test description",
               startTime,  endTime, null,
                new Location(null, null, null, "CHCH", null, "NZ"));


        teamRepository.save(team);
        userRepository.save(user);
        activityRepository.save(activity);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    @Given("I am a manager or coach,")
    public void i_manage_a_team() {
        Assertions.assertTrue(team.isManager(user));
    }

    @Given("I am viewing an activity of the type Game or Friendly")
    public void i_am_viewing_an_activity_of_the_type_game_or_friendly() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(activity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @When("the activity has begun")
    public void the_activity_has_begun() {
        Assertions.assertTrue(LocalDateTime.now().isAfter(startTime));
    }

    @When("there is a current overall score")
    public void there_is_a_current_overall_score() throws Exception {
        mockMvc.perform(post("/overall-score")
                .param("actId", String.valueOf(activity.getId()))
                .param("overallScoreTeam", "5")
                .param("overallScoreOpponent", "10"));

    }

    @Then("I am able to update the overall score again")
    public void i_am_able_to_update_the_overall_score_again() throws Exception {
        mockMvc.perform(post("/overall-score")
                .param("actId", String.valueOf(activity.getId()))
                .param("overallScoreTeam", "5")
                .param("overallScoreOpponent", "10"));


        verify(activityService, times(2)).updateOrAddActivity(any());
    }

    @When("I am adding the overall score")
    public void i_am_adding_the_overall_score() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(activity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @When("I enter the score in the format 5 for Team A and 7 for Team B, or 5-6 for Team A and 7-6 for Team B")
    public void i_enter_the_score_in_the_format_for_team_a_and_for_team_b_or_for_team_a_and_for_team_b() throws Exception {
        mockMvc.perform(post("/overall-score")
                .param("actId", String.valueOf(activity.getId()))
                .param("overallScoreTeam", "5-6")
                .param("overallScoreOpponent", "7-6"));
    }

    @Then("the application accepts the scores as the format matches")
    public void the_application_accepts_the_scores_as_the_format_matches() {
        verify(activityService, times(1)).updateOrAddActivity(any());
    }

    @When("I enter the score in the format 5 for Team A and 7-6 for Team B")
    public void i_enter_the_score_in_the_format_for_team_a_and_for_team_b() throws Exception {
        mockMvc.perform(post("/overall-score")
                .param("actId", String.valueOf(activity.getId()))
                .param("overallScoreTeam", "5")
                .param("overallScoreOpponent", "7-6"));
    }

    @Then("the application does not accept the scores as the format does not match and an error message displays telling the user that the score formats do not match")
    public void the_application_doesn_t_accept_the_scores_as_the_format_doesn_t_match_and_an_error_message_displays_telling_the_user_that_error_the_score_formats_do_not_match() {
        verify(activityService, times(0)).updateOrAddActivity(any());

    }


    @Given("I am adding a score")
    public void i_am_adding_a_score() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(activity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }



    @When("I do not enter a value into the goal value field \\(and rest of form is right)")
    public void i_do_not_enter_a_value_into_the_goal_value_field_and_rest_of_form_is_right() throws Exception {
        when(activityService.checkTimeOfFactWithinActivity(activity, 4)).thenReturn(true);

        mockMvc.perform(post("/add-goal")
                .param("actId", String.valueOf(activity.getId()))
                .param("scorer", "1")
                .param("goalValue", "")
                .param("time", "4")
                .param("description", ""));

    }

    @Then("the system accepts and uses a default value of 1")
    public void the_system_accepts_and_uses_a_default_value_of() {


        verify(activityService, times(1)).updateOrAddActivity(any());

        System.out.println("fact list" + activity.getFactList().get(0));


    }

    @When("I enter a positive integer \\(given rest of form is valid)")
    public void i_enter_a_positive_integer_given_rest_of_form_is_valid() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the system accepts")
    public void the_system_accepts() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I enter a value that is not a positive integer")
    public void i_enter_a_value_that_is_not_a_positive_integer() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the system does not accept and an error message displays telling the user to enter a positive integer.")
    public void the_system_does_not_accept_and_an_error_message_displays_telling_the_user_to_enter_a_positive_integer() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
