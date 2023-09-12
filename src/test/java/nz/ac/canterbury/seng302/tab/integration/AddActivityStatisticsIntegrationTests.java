package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.*;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.fact.Fact;
import nz.ac.canterbury.seng302.tab.enums.ActivityOutcome;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class AddActivityStatisticsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;
    @SpyBean
    private LineUpService lineUpService;

    @SpyBean
    private LineUpPositionService lineUpPositionService;


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

    private LineUpRepository lineUpRepository;

    private LineUpPositionRepository lineUpPositionRepository;


    private User user;

    private User user2;

    private Team team;

    private Activity activity;

    private Activity otherActivity;

    private FactRepository factRespository;


    private ActivityRepository activityRepository;

    LocalDateTime startTime;

    private Fact fact;

    private String description;


    @Before("@add_activity_stats")
    public void setup() throws IOException {

        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        factRespository = applicationContext.getBean(FactRepository.class);
        lineUpPositionRepository = applicationContext.getBean(LineUpPositionRepository.class);
        lineUpRepository = applicationContext.getBean(LineUpRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository, lineUpRepository, lineUpPositionRepository));
        factService= Mockito.spy(new FactService(factRespository));
        lineUpService=Mockito.spy(new LineUpService(lineUpRepository));
        lineUpPositionService = Mockito.spy(new LineUpPositionService(lineUpPositionRepository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivityController(userService,activityService,teamService,factService, lineUpService, lineUpPositionService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);



        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        Location testLocation2 = new Location(null, null, null, "CHCH", null, "NZ");

        startTime =  LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime endTime = LocalDateTime.of(2023, 1,1,8,30);

        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("test1", "Hockey", new Location(null, null, null, "chch", null, "nz"), user);
        activity = new Activity(ActivityType.Game, null, "Test description",
               startTime,  endTime, null,
                new Location(null, null, null, "CHCH", null, "NZ"));
        otherActivity = new Activity(ActivityType.Other, null, "testing", startTime, endTime, null,
                new Location(null, null, null, "chch", null, "nz"));

        user2 = new User("Sally", "Smith", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "sally@example.com", "Password123!", testLocation2);

        teamRepository.save(team);

        user2.joinTeam(team);
        userRepository.save(user);
        userRepository.save(user2);

        activityRepository.save(activity);
        activityRepository.save(otherActivity);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

        when(userService.findUserById(Long.parseLong("3"))).thenReturn(Optional.of(user2));

    }

    @Given("I am a manager or coach,")
    public void i_manage_a_team() {
        Assertions.assertTrue(team.isManager(user));
    }

    @Given("I am viewing an activity of the type Game or Friendly")
    public void i_am_viewing_an_activity_of_the_type_game_or_friendly() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(activity.getId())))
                .andExpect(status().isOk())
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
                .andExpect(status().isOk())
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

    @Given("I am viewing an activity of any type")
    public void i_am_viewing_an_activity_of_any_type() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(otherActivity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Then("I am able to record facts through a dedicated UI element.")
    public void i_am_able_to_record_facts_through_a_dedicated_ui_element() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(otherActivity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @When("I am adding a fact about the activity")
    public void i_am_adding_a_fact_about_the_activity() {
        fact = new Fact(null, "I fell over", otherActivity);
    }

    @Then("I must fill out the required field of description and optionally the time it occurred.")
    public void i_must_fill_out_the_required_field_of_description_and_optionally_the_time_it_occurred() throws Exception {
        mockMvc.perform(post("/add-fact")
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", fact.getTimeOfEvent())
                        .param("description", fact.getDescription()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }


    @Given("I am adding a score")
    public void i_am_adding_a_score() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(activity.getId())))
                .andExpect(status().isOk())
                .andReturn();
    }



    @When("I do not enter a value into the goal value field \\(and rest of form is right)")
    public void i_do_not_enter_a_value_into_the_goal_value_field_and_rest_of_form_is_right() throws Exception {
        when(activityService.checkTimeOfFactWithinActivity(activity, 4)).thenReturn(true);
        when(userService.findUserById(Long.parseLong("3"))).thenReturn(Optional.of(user2));
        mockMvc.perform(post("/add-goal")
                .param("actId", String.valueOf(activity.getId()))
                .param("scorer", "3")
                .param("goalValue", "")
                .param("time", "4")
                .param("description", ""));
    }

    @Then("the system accepts and uses a default value of 1")
    public void the_system_accepts_and_uses_a_default_value_of() {
        verify(activityService, times(1)).updateOrAddActivity(any());
    }

    @When("I enter a positive integer \\(given rest of form is valid)")
    public void i_enter_a_positive_integer_given_rest_of_form_is_valid() throws Exception {
        mockMvc.perform(post("/add-goal")
                .param("actId", String.valueOf(activity.getId()))
                .param("scorer", "3")
                .param("goalValue", "1")
                .param("time", "4")
                .param("description", ""));
    }

    @Then("the system accepts")
    public void the_system_accepts() throws Exception {
        mockMvc.perform(post("/add-goal")
                .param("actId", String.valueOf(activity.getId()))
                .param("scorer", "3")
                .param("goalValue", "1")
                .param("time", "4")
                .param("description", "")).andExpect(status().isFound());
    }


    @And("I am viewing an activity of the type ‘Game’ or ‘Friendly’")
    public void iAmViewingAnActivityOfTheTypeGameOrFriendly() {
        Assertions.assertTrue((activity.getActivityType() == ActivityType.Game) || activity.getActivityType() == ActivityType.Friendly);
    }

    @When("the activity has ended,")
    public void theActivityHasEnded() {
        Assertions.assertTrue(LocalDateTime.now().isAfter(activity.getActivityEnd()));
    }

    @Then("I am able to add an outcome for the overall activity through a dedicated UI element")
    public void iAmAbleToAddAnOutcomeForTheOverallActivityThroughADedicatedUIElement() throws Exception {
        mockMvc.perform(post("/add-outcome")
                .param("actId", String.valueOf(activity.getId()))
                .param("activityOutcomes", String.valueOf(ActivityOutcome.Win))).andExpect(status().isFound());
    }

    @And("I am adding a goal, substitution or fact")
    public void iAmAddingAGoalSubstitutionOrFact() {
        fact = new Fact("1", "abc", activity);
    }

    @And("I specify a time")
    public void iSpecifyATime() {
        fact.setTimeOfEvent("999999999999999999999999");
    }


    @Then("the time must fall within the bounds of the activity \\(ie cannot be before the beginning or after the end)")
    public void theTimeMustFallWithinTheBoundsOfTheActivityIeCannotBeBeforeTheBeginningOrAfterTheEnd() throws Exception {
        mockMvc.perform(post("/add-fact")
                        .param("actId", String.valueOf(otherActivity.getId()))
                        .param("timeOfFact", fact.getTimeOfEvent())
                        .param("description", fact.getDescription()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();
    }

    @And("I am adding a description to the fact type ‘Fact’ or ‘Substitution’ or ‘Goal’,")
    public void iAmAddingADescriptionToTheFactTypeFactOrSubstitutionOrGoal() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(otherActivity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @And("I enter a description longer than {int} characters,")
    public void iEnterADescriptionLongerThanCharacters(int arg0) {
        description = "aa".repeat(arg0);
    }

    @When("I click the ‘Add Fact’ or ‘Add Substitution’ or ‘Add Goal’ button,")
    public void iClickTheAddFactOrAddSubstitutionOrAddGoalButton() throws Exception {
        mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(otherActivity.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Then("an error message tells me that the description must be {int} characters or less")
    public void anErrorMessageTellsMeThatTheDescriptionMustBeCharactersOrLess(int arg0) throws Exception {
        mockMvc.perform(post("/add-fact")
                        .param("actId", String.valueOf(activity.getId()))
                        .param("timeOfFact", "5")
                        .param("description", description))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();
    }
}
